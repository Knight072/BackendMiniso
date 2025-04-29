package co.miniso.rompefilas.service;

import co.miniso.rompefilas.db1.model.BillApp;
import co.miniso.rompefilas.db1.model.InvoiceDetailApp;
import co.miniso.rompefilas.db1.model.InvoiceDetailAppId;
import co.miniso.rompefilas.db1.model.Tenant;
import co.miniso.rompefilas.db1.repository.BillAppRepository;
import co.miniso.rompefilas.db1.repository.TenantRepository;
import co.miniso.rompefilas.db3.model.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Service
public class BillingService {

    private static final Logger logger = LoggerFactory.getLogger(BillingService.class);

    private final BillAppRepository billAppRepository;
    private final TenantRepository tenantRepository;

    public BillingService(BillAppRepository billAppRepository, TenantRepository tenantRepository) {
        this.billAppRepository = billAppRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public int saveBill(BillRequest billRequest) {
        Optional<BillApp> existingBill = billAppRepository.findById(billRequest.getBill().getNumAtCard());
        if (existingBill.isPresent()) {
            logger.warn("Factura con NumAtCard {} ya existe. No se guardará.", billRequest.getBill().getNumAtCard());
            return -1; // Retorna un valor de error si la factura ya existe
        }

        savingBill(billRequest.getBill());
        processBill(billRequest.getBill(), billRequest.getClient());

        tenantRepository.increaseConsecutive(billRequest.getBill().getStore());

        return tenantRepository.getConsecutive(billRequest.getBill().getStore());
    }

    /*@Transactional
    private void billingSavingThread(BillRequest billRequest) {
        CountDownLatch latch = new CountDownLatch(2); // Usar CountDownLatch para gestionar la sincronización

        Thread savingThread = new Thread(() -> {
            try {
                logger.info("Hilo de guardado iniciado para factura {}", billRequest.getBill().getNumAtCard());
                savingBill(billRequest.getBill());// Guardado en DB de la factura
                logger.info("Factura {} guardada correctamente", billRequest.getBill().getNumAtCard());
            } catch (Exception e) {
                logger.error("Error al guardar la factura {}: {}", billRequest.getBill().getNumAtCard(), e.getMessage(), e);
            } finally {
                latch.countDown();
            }
        });

        Thread processThread = new Thread(() -> {
            try {
                logger.info("Hilo de procesamiento iniciado para factura {}", billRequest.getBill().getNumAtCard());
                processBill(billRequest.getBill(), billRequest.getClient());
                logger.info("Factura {} procesada correctamente", billRequest.getBill().getNumAtCard());
            } catch (Exception e) {
                logger.error("Error al procesar la factura {}: {}", billRequest.getBill().getNumAtCard(), e.getMessage(), e);
            } finally {
                latch.countDown();
            }
        });

        savingThread.start();
        processThread.start();

        // Esperamos a que ambos hilos terminen (sincronización)
        try {
            latch.await();
            logger.info("Ambos procesos completados para factura {}", billRequest.getBill().getNumAtCard());
        } catch (InterruptedException e) {
            logger.error("La sincronización de hilos fue interrumpida: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }*/

    @Transactional
    private void savingBill(BillApp billApp) {
        // Asigna IDs a los productos y enlázalos con la factura
        int lineas = 0;
        for (InvoiceDetailApp detalle : billApp.getProductos()) {
            detalle.setId(new InvoiceDetailAppId(billApp.getNumAtCard(), lineas, detalle.getSku()));
            detalle.setLineas(lineas);
            detalle.setBillApp(billApp);
            lineas++;
        }
        billAppRepository.save(billApp);
        logger.info("Factura {} guardada con {} productos.", billApp.getNumAtCard(), billApp.getProductos().size());
    }

    @Transactional
    private void processBill(BillApp billApp, Client client) {
        Tenant store = tenantRepository.getReferenceById(Integer.toString(billApp.getStore()));
        String tipoDoc = billApp.getTipoDocumento();
        String doc = client.getDocument();
        String codigoDaneCliente = String.valueOf(store.getCodeMunicipality());
        String region = "CO-Colombia|" + store.getDepartment() + "|" + store.getMunicipality();

        String FederalTaxID = "";
        if (tipoDoc.equals("NIT") || tipoDoc.equals("CC")) {

            int[] primos = {71, 67, 59, 53, 47, 43, 41, 37, 29, 23, 19, 17, 13, 7, 3};
            int sumatoria = 0;

            int contDos = doc.length();
            while (contDos < 15) {
                doc = "0" + doc;
                contDos += 1;
            }

            for (int i = 0; i < 15; i++) {
                sumatoria = sumatoria + Integer.parseInt(String.valueOf(doc.charAt(i))) * primos[i];
            }
            sumatoria = sumatoria % 11;

            sumatoria = switch (sumatoria) {
                case 10, 1 -> 1;
                case 0 -> 0;
                default -> 11 - sumatoria;
            };

            FederalTaxID = String.valueOf(sumatoria);
        }

        //String fechaEmisionT = fechaEmision.replace(" ", "T");
        String condicionPago = "";
        String infoAdicional = "";
        Double impuestoBolsaCalculo = 0.0;
        double totalBolsas = 0.0;
        double valorBolsaUnitario = 0.0;
        String lineas = "";
        String cabeceroImpuesto = "";
        String lineasImpuesto = "";
        double totalIva = 0.0;
        double totalImpo = 0.0;
        double totalLineaP = 0.0;
        double totalLineaPSinAtiucloImp = 0.0;
        double totalDescuento = 0.0;
        String FV_TRB_Id_Tributos = null;
        double bruto = 0.0;
        double brutoDos = 0.0;
        Double calculoTotal = 0.0;
        Double TaxInclusiveAmount = 0.0;
        String AllowedChargeCab = "";

        Set<Integer> tipoImpuestoSet = new HashSet<>();

        for (InvoiceDetailApp i : billApp.getProductos()) {
            tipoImpuestoSet.add(i.getImpuesto());
        }

        ArrayList<Integer> tipoImpuesto = new ArrayList<>(tipoImpuestoSet);

        String detalleDescuento = "			<tem:AllowedChargeDet>\r\n";

        int tamanoReal = billApp.getProductos().toArray().length;
        int cont = 1;
        //recordatorio: guardar el global.getImpuestoBolsa en base
        for (InvoiceDetailApp i : billApp.getProductos()) {
            cont += 1;
            boolean esBolsa = i.getSku().equals("0000000102") || i.getSku().equals("0000000103");
            double impuestoCal = 0.0;
            double impuestoImpo = 0.0;
            int imp = i.getImpuesto();
            System.out.println(imp);
            if (esBolsa) {
                totalDescuento += 0.0;
                totalBolsas += i.getPrecioSinImpuesto() * i.getCantidad();

            } else {
                totalDescuento += i.getValorDescuento();
            }

            if (imp == 19 || imp == 0 || imp == 66) {

                if (esBolsa) {
                    double valor = (i.getPrecioSinImpuesto() - 66) / (0.19 + 1);
                    valorBolsaUnitario += ((i.getPrecioSinImpuesto() - 66) / (0.19 + 1)) * i.getCantidad();
                    impuestoCal = valor * 0.19 * i.getCantidad();
                } else {
                    impuestoCal = i.getTotalLinea() * i.getImpuesto() / 100;
                }
                totalIva += impuestoCal;
            } else {
                impuestoImpo = i.getTotalLinea() * i.getImpuesto() / 100;
                impuestoCal = 0;
                totalImpo += impuestoImpo;
            }

            if (imp == 19 || esBolsa) {
                FV_TRB_Id_Tributos = "01";
            }

            if (imp == 8) {
                FV_TRB_Id_Tributos = "4";
            }

            if (imp == 19 || imp == 8) {
                totalLineaPSinAtiucloImp += i.getTotalLinea();
            } else if (esBolsa) {
                totalLineaPSinAtiucloImp += ((i.getPrecioSinImpuesto() - 66) / (0.19 + 1)) * i.getCantidad();
            }

            totalLineaP += i.getTotalLinea();

            if (esBolsa) {
                double valor = ((i.getPrecioSinImpuesto() - 66) / (0.19 + 1));
                bruto += valor * i.getCantidad();

            } else {
                bruto += i.getPrecioSinImpuesto() * i.getCantidad();
            }

            if (imp != 0) {
                brutoDos += i.getPrecioSinImpuesto() * i.getCantidad();
            }
            boolean estado = true;

            String code = i.getCodigoBarras();

            if(code.isEmpty()) {
                code = i.getSku();
            }

            /*String articuloSinCaracter = articulo.get(i);

            if(articuloSinCaracter.contains("&")) {
                articuloSinCaracter.replaceAll("&", "y");
            }
//				if (articuloSinCaracter.contains("PELUCHE PARADO 11IN WARM & SOFT SERIES ROSA")) {
//					articuloSinCaracter="PELUCHE PARADO 11IN WARM y SOFT SERIES ROSA";
//				}

            Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\s]");
            Matcher matcher = pattern.matcher(articuloSinCaracter);
            if (matcher.find()) {
                articuloSinCaracter = articuloSinCaracter.replaceAll("[^a-zA-Z0-9\\s]", "");
            }

//				if (articuloSinCaracter.contains("Ã—")) {
//					articuloSinCaracter = articuloSinCaracter.replaceAll("Ã—", "");
//				}

            if(imp==19) {
                calculoTotal+=Double.parseDouble(totalLinea.get(i))+impuestoCal;
                TaxInclusiveAmount+=Double.parseDouble(totalLinea.get(i))+impuestoCal;
            }else if(skus.get(i).equals("0000000102")||skus.get(i).equals("0000000103")) {
                TaxInclusiveAmount+=(Double.parseDouble(totalLinea.get(i))+ Double.parseDouble(precioSinIm.get(i)))*Integer.parseInt(cantidad.get(i));
                calculoTotal+=Double.parseDouble(totalLinea.get(i));
            }else if (imp==8) {
                calculoTotal+=Double.parseDouble(totalLinea.get(i))+impuestoImpo;
                TaxInclusiveAmount+=Double.parseDouble(totalLinea.get(i))+impuestoImpo;
            }else {
                calculoTotal+=Double.parseDouble(totalLinea.get(i))+impuestoImpo;
                TaxInclusiveAmount+=Double.parseDouble(totalLinea.get(i))+impuestoCal;
            }*/
        }
    }
}