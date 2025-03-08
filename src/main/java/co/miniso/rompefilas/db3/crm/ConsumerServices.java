package co.miniso.rompefilas.db3.crm;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.*;

import co.miniso.rompefilas.db3.crm.Exceptions.BadEmailException;
import co.miniso.rompefilas.db3.crm.Exceptions.ExistEmailException;
import co.miniso.rompefilas.db3.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class that consume SOAP services to perform some important actions
 *
 * @author santiago.forero
 */
@Component
public class ConsumerServices {

    private final ClientRepository clientRepository;

    @Autowired
    ConsumerServices(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    /**
     * method that update a Customer using a SOAP service
     *
     * @return an integer that is the ResponseCode
     * @throws IOException
     * @throws ExistEmailException
     */
    public int updateCustomer(String customerno, String name, String gender, String lastName, String email, String oldDoc,
                              String docType, String newDoc, String recordStateEmail, String recordStateAltKey)
            throws IOException, BadEmailException, ExistEmailException {
        String xmlSaveSoap;
        if (emailVerify(email)) {
            if (newDoc.matches("111111101")) emailExistVerify(email);
            xmlSaveSoap = this.generateXMLSave(customerno, name, gender,
                    lastName, email, oldDoc, docType, newDoc, recordStateEmail, recordStateAltKey);
        } else {
            throw new BadEmailException(BadEmailException.BAD_EMAIL);
        }
        HttpURLConnection connection = getHttpURLConnection(xmlSaveSoap);

        int responseCode = connection.getResponseCode();
        // Si el código de respuesta es 200, leer la respuesta
        if (responseCode == HttpURLConnection.HTTP_OK) {

        } else {
            // Si el código de respuesta es un error (por ejemplo, 500), leer el error
            try (InputStream is = connection.getErrorStream()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String response;
                List<String> items = new ArrayList<>();
                while ((response = br.readLine()) != null) {
                    // Expresion regular para extraer los valores de CustomerNumber
                    Pattern pattern = Pattern.compile("<Message>(.*?)</Message>");
                    Matcher matcher = pattern.matcher(response);
                    while (matcher.find()) {
                        items.add(matcher.group(1)); // Obtiene el contenido entre <Message>...</Message>
                    }
                }
            }
        }
        return responseCode;
    }

    private static HttpURLConnection getHttpURLConnection(String xmlSaveSoap) throws IOException {
        URL url = new URL("http://mns-crm/CRMWebService/CrmService.svc/basic");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        connection.setRequestProperty("SOAPAction", "http://epicor.com/retail/CRM/7.0.0/ICrmService/Save");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = xmlSaveSoap.getBytes("UTF-8");
            os.write(input, 0, input.length);
        }
        return connection;
    }

    /**
     * method that create a customer using a SOAP Service
     *
     * @return an Integer that is the Response Code
     * @throws IOException
     * @throws ExistEmailException
     */
    public int createUser(String name, String lastName, String gender, String email, String docType, String newDoc
    ) throws IOException, IllegalStateException, ExistEmailException, BadEmailException {
        String xmlSoapAdd;
        if (emailVerify(email)) {
            xmlSoapAdd = this.generateSaveNewXML(name, lastName, gender, email, docType, newDoc);
        } else {
            throw new BadEmailException(BadEmailException.BAD_EMAIL);
        }
        HttpURLConnection connection = getUrlConnection(xmlSoapAdd);

        try {
            int responseCode = connection.getResponseCode();

            // Si el código de respuesta es 200, leer la respuesta
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = connection.getInputStream()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String response;
                    List<String> items = new ArrayList<>();
                    while ((response = br.readLine()) != null) {
                        // Expresion regular para extraer los valores de CustomerNumber
                        Pattern pattern = Pattern.compile("<a:CustomerNumber>(.*?)</a:CustomerNumber>");
                        Matcher matcher = pattern.matcher(response);
                        while (matcher.find()) {
                            items.add(matcher.group(1)); // Obtiene el contenido entre <CustomerNumber>...</CustomerNumber>
                        }

                        responseCode = updateCustomer(items.get(0), name, gender, lastName, email, newDoc, docType, newDoc, "Added", "Added");
                        if (responseCode == 500) throw new ExistEmailException(ExistEmailException.EXIST_EMAIL);
                    }
                }
            } else {
                // Si el código de respuesta es un error (por ejemplo, 500), leer el error
                try (InputStream is = connection.getErrorStream()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String response;
                    while ((response = br.readLine()) != null) {
                        System.out.println("Error response: " + response); // Imprimir el error del servidor
                    }
                }
            }

            return responseCode; // Retornar el código de respuesta

        } catch (IOException e) {
            throw e; // Re-lanzar la excepción
        }
    }

    private static HttpURLConnection getUrlConnection(String xmlSoapAdd) throws IOException {
        URL url = new URL("http://mns-crm/CRMWebService/CrmService.svc/basic");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        connection.setRequestProperty("SOAPAction", "http://epicor.com/retail/CRM/7.0.0/ICrmService/SaveNewCustomer");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = xmlSoapAdd.getBytes("UTF-8");
            os.write(input, 0, input.length);
        }
        return connection;
    }

    private boolean emailVerify(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" // Empieza con caracteres válidos para la parte local
                + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z]{2,}$"; // Luego el dominio y su TLD (con mínimo 2 caracteres)
        return email.matches(regex);
    }

    private void emailExistVerify(String email) throws ExistEmailException {
        if (clientRepository.validEmail(email) == 1) throw new ExistEmailException(ExistEmailException.EXIST_EMAIL);
    }


    /**
     * method that creates the XML to the SOAP service save
     *
     * @param customerno
     * @param name
     * @param lastName
     * @param email
     * @param oldDoc
     * @param docType
     * @param newDoc
     * @return String
     */
    private String generateXMLSave(String customerno, String name, String gender,
                                   String lastName, String email, String oldDoc,
                                   String docType, String newDoc, String recordStateEmail,
                                   String recordStateCustomerAltKey) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://epicor.com/retail/CRM/7.0.0/\" xmlns:epic=\"http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService\">\r\n"
                + "   <soapenv:Header/>\r\n"
                + "   <soapenv:Body>\r\n"
                + "      <ns:Save xmlns=\"http://epicor.com/retail/CRM/7.0.0/\">\r\n"
                + "         <!--Optional:-->\r\n"
                + "         <ns:parameters xmlns:b=\"http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:CustomerNumber>" + customerno + "</epic:CustomerNumber>\r\n"
                + "            <!--Always 2-->\r\n"
                + "            <epic:DatabaseGroupID>2</epic:DatabaseGroupID>\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:LanguageID>-1</epic:LanguageID>\r\n"
                + "            <!--Always 9999-->\r\n"
                + "            <epic:SalesAssociateNumber>9999</epic:SalesAssociateNumber>\r\n"
                + "            <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "            <epic:StoreNumber>9010</epic:StoreNumber>\r\n"
                + "         </ns:parameters>\r\n"
                + "         <!--Optional:-->\r\n"
                + "         <ns:checkDuplicates>false</ns:checkDuplicates>\r\n"
                + "         <!--Optional:-->\r\n"
                + "		 <ns:customer xmlns:b=\"http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
                + "             <!--Optional:-->\r\n"
                + "            <epic:AlphaKey/>\r\n"
                + "            <!--BirthDate:-->\r\n"
                + "            <epic:BirthDate>1993-01-07T00:00:00</epic:BirthDate>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:CompanyName/>\r\n"
                + "            <!--Current date and time:-->\r\n"
                + "            <epic:CreateDate>2024-12-01T00:00:00</epic:CreateDate>\r\n"
                + "            <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "            <epic:CreateStoreNumber>9010</epic:CreateStoreNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:CreateUser/>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:CreatedSource/>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:CustomerDivisions>\r\n"
                + "                    <!--Zero or more repetitions:-->\r\n"
                + "               <epic:CustomerDivisionWCF>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:CreateDate>2024-12-01T00:00:00</epic:CreateDate>\r\n"
                + "                  <!--For APP=APP MINISO, For Ecommerce= Ecommerce-->\r\n"
                + "                  <epic:CreateSource>APP MINISO</epic:CreateSource>\r\n"
                + "                  <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "                  <epic:CreateStoreNumber>9010</epic:CreateStoreNumber>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:CreateUser/>\r\n"
                + "                  <!--TBD-->\r\n"
                + "                  <epic:DivisionID>7</epic:DivisionID>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:LastMailingDate>2024-12-01T00:00:00</epic:LastMailingDate>\r\n"
                + "                  <!--Always -1-->\r\n"
                + "                  <epic:MaxNumberOfMailings>-1</epic:MaxNumberOfMailings>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:ModifyDate>2024-12-01T00:00:00</epic:ModifyDate>\r\n"
                + "                  <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "                  <epic:ModifyStoreNumber>9010</epic:ModifyStoreNumber>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:ModifyUser/>\r\n"
                + "                  <!--Always 0-->\r\n"
                + "                  <epic:NumberOfMailings>0</epic:NumberOfMailings>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:PrimaryAddressDate>2024-12-01T00:00:00</epic:PrimaryAddressDate>\r\n"
                + "                  <!--Always -1-->\r\n"
                + "                  <epic:PrimaryAddressID>-1</epic:PrimaryAddressID>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:PrimaryEmailDate>2024-12-01T00:00:00</epic:PrimaryEmailDate>\r\n"
                + "                  <!--Always -1:-->\r\n"
                + "                  <epic:PrimaryEmailID>-1</epic:PrimaryEmailID>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:PrimaryPhoneDate>2024-12-01T00:00:00</epic:PrimaryPhoneDate>\r\n"
                + "                  <!--Always -1:-->\r\n"
                + "                  <epic:PrimaryPhoneID>-1</epic:PrimaryPhoneID>\r\n"
                + "                  <!--Always Modified-->\r\n"
                + "                  <epic:RecordState>Modified</epic:RecordState>\r\n"
                + "                  <!--Always 9999-->\r\n"
                + "                  <epic:SalesAssociateNumber>1508</epic:SalesAssociateNumber>\r\n"
                + "                  <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "                  <epic:StoreNumber>9010</epic:StoreNumber>\r\n"
                + "               </epic:CustomerDivisionWCF>\r\n"
                + "            </epic:CustomerDivisions>\r\n"
                + "            <!--Always 0-->\r\n"
                + "            <epic:DistributionStatusCode>0</epic:DistributionStatusCode>\r\n"
                + "            <!--FirstName-->\r\n"
                + "            <epic:FirstName>" + name + "</epic:FirstName>\r\n"
                + "            <!--Gender-->\r\n"
                + "            <epic:Gender>" + gender + "</epic:Gender>\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:ID>-1</epic:ID>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:JobTitle/>\r\n"
                + "            <!--Always SPA-->\r\n"
                + "            <epic:LanguageCode>SPA</epic:LanguageCode>\r\n"
                + "            <!--Current date and time:-->\r\n"
                + "            <epic:LastMailingDate>2024-12-01T00:00:00</epic:LastMailingDate>\r\n"
                + "            <!--LastName-->\r\n"
                + "            <epic:LastName>" + lastName + "</epic:LastName>\r\n"
                + "            <!--Current date and time:-->\r\n"
                + "            <epic:LastUpdateDate>2024-12-01T00:00:00</epic:LastUpdateDate>\r\n"
                + "            <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "            <epic:LastUpdateStoreNumber>9010</epic:LastUpdateStoreNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:LastUpdateUser/>\r\n"
                + "            <!--MaritalStatus S or M-->\r\n"
                + "            <epic:MaritalStatus>S</epic:MaritalStatus>\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:NextAddressID>-1</epic:NextAddressID>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:Number/>\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:NumberMailings>-1</epic:NumberMailings>\r\n"
                + "            <!--Always 1-->\r\n"
                + "            <epic:OriginalDivisionID>1</epic:OriginalDivisionID>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:PhoneticKey/>\r\n"
                + "            <!--Always Modified-->\r\n"
                + "            <epic:RecordState>Modified</epic:RecordState>\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:SalesAssociateNumber>-1</epic:SalesAssociateNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:Salutation/>\r\n"
                + "            <!--Always A-->\r\n"
                + "            <epic:StatusCode>A</epic:StatusCode>\r\n"
                + "            <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "            <epic:StoreNumber>9010</epic:StoreNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:TitleCode/>\r\n"
                + "			</ns:customer>\r\n"
                + "			<!--Optional:-->\r\n"
                + "			<ns:customerAddresses xmlns:b=\"http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
                + "            <!--Always -1:-->\r\n"
                + "            <epic:CustomerNumber>" + customerno + "</epic:CustomerNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "         </ns:customerAddresses>\r\n"
                + "         <ns:customerEmails xmlns:b=\"http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
                + "                <!--Optional:-->\r\n"
                + "            <epic:CustomerNumber>" + customerno + "</epic:CustomerNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:Items>\r\n"
                + "                    <!--Zero or more repetitions:-->\r\n"
                + "               <epic:CustomerEmailWCF>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:CreateDate>2020-10-23T00:00:00</epic:CreateDate>\r\n"
                + "                  <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "                  <epic:CreateStoreNumber>2203</epic:CreateStoreNumber>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:CreateUser i:nil=\"true\"/>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:DateLastModified>2020-10-23T00:00:00</epic:DateLastModified>\r\n"
                + "                  <!--EmailAddress-->\r\n"
                + "                  <epic:EmailAddress>" + email + "</epic:EmailAddress>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <!--always 9-->\r\n"
                + "                  <epic:EmailIndicatorID>9</epic:EmailIndicatorID>\r\n"
                + "                  <!--always PERS-->\r\n"
                + "                  <epic:EmailTypeCode>PERS</epic:EmailTypeCode>\r\n"
                + "                  <!--always -1-->\r\n"
                + "                  <epic:ID>-1</epic:ID>\r\n"
                + "                  <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "                  <epic:ModifyStoreNumber>2203</epic:ModifyStoreNumber>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:ModifyUser i:nil=\"true\"/>\r\n"
                + "                  <!--always Added-->\r\n"
                + "                  <epic:RecordState>" + recordStateEmail + "</epic:RecordState>\r\n"
                + "               </epic:CustomerEmailWCF>\r\n"
                + "            </epic:Items>\r\n"
                + "         </ns:customerEmails>\r\n"
                + "         <!--Optional:-->\r\n"
                + "         <!--Optional:-->\r\n"
                + "         \r\n"
                + "         <ns:customerAlternateKeys>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:CustomerNumber>" + customerno + "</epic:CustomerNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:Items>\r\n"
                + "               <!--Zero or more repetitions:-->\r\n"
                + "               <epic:CustomerAlternateKeyWCF>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:Code>" + docType + "</epic:Code>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:DateAdded>2020-10-23T00:00:00</epic:DateAdded>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:DisplayValue>" + newDoc + "</epic:DisplayValue>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:OriginalCode>" + docType + "</epic:OriginalCode>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:OriginalValue>" + oldDoc + "</epic:OriginalValue>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:RecordState>" + recordStateCustomerAltKey + "</epic:RecordState>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:Value>" + newDoc + "</epic:Value>\r\n"
                + "               </epic:CustomerAlternateKeyWCF>\r\n"
                + "            </epic:Items>\r\n"
                + "         </ns:customerAlternateKeys>\r\n"
                + "         <!--Optional:-->\r\n"
                + "      </ns:Save>\r\n"
                + "   </soapenv:Body>\r\n"
                + "</soapenv:Envelope>";
    }

    /**
     * method that generates the XML to SOAP Service SaveNewCustomer
     *
     * @param customerno
     * @param name
     * @param lastName
     * @param email
     * @param oldDoc
     * @param docType
     * @param newDoc
     * @return String
     */
    private String generateSaveNewXML(String name, String lastName, String gender,
                                      String email, String docType, String newDoc) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://epicor.com/retail/CRM/7.0.0/\" xmlns:epic=\"http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService\">\r\n"
                + "   <soapenv:Header/>\r\n"
                + "   <soapenv:Body>\r\n"
                + "      <ns:SaveNewCustomer xmlns=\"http://epicor.com/retail/CRM/7.0.0/\">\r\n"
                + "            <!--Optional:-->\r\n"
                + "         <ns:parameters xmlns:b=\"http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:CustomerNumber>-1</epic:CustomerNumber>\r\n"
                + "            <!--Always 2-->\r\n"
                + "            <epic:DatabaseGroupID>2</epic:DatabaseGroupID>\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:LanguageID>-1</epic:LanguageID>\r\n"
                + "            <!--Always 9999-->\r\n"
                + "            <epic:SalesAssociateNumber>9999</epic:SalesAssociateNumber>\r\n"
                + "            <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "            <epic:StoreNumber>2203</epic:StoreNumber>\r\n"
                + "         </ns:parameters>\r\n"
                + "         <!--Always true-->\r\n"
                + "         <ns:checkDuplicates>true</ns:checkDuplicates>\r\n"
                + "         <!--Optional:-->\r\n"
                + "         <ns:customer xmlns:b=\"http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
                + "                <!--Optional:-->\r\n"
                + "            <epic:AlphaKey/>\r\n"
                + "            <!--BirthDate:-->\r\n"
                + "            <epic:BirthDate>1993-04-22T00:00:00</epic:BirthDate>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:CompanyName/>\r\n"
                + "            <!--Current date and time:-->\r\n"
                + "            <epic:CreateDate>2024-12-01T00:00:00</epic:CreateDate>\r\n"
                + "            <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "            <epic:CreateStoreNumber>2203</epic:CreateStoreNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:CreateUser/>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:CreatedSource/>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:CustomerDivisions>\r\n"
                + "                    <!--Zero or more repetitions:-->\r\n"
                + "               <epic:CustomerDivisionWCF>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:CreateDate>2024-12-01T00:00:00</epic:CreateDate>\r\n"
                + "                  <!--For APP=APP MINISO, For Ecommerce= Ecommerce-->\r\n"
                + "                  <epic:CreateSource>APP MINISO</epic:CreateSource>\r\n"
                + "                  <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "                  <epic:CreateStoreNumber>2203</epic:CreateStoreNumber>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:CreateUser/>\r\n"
                + "                  <!--TBD-->\r\n"
                + "                  <epic:DivisionID>7</epic:DivisionID>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:LastMailingDate>2024-12-01T00:00:00</epic:LastMailingDate>\r\n"
                + "                  <!--Always -1-->\r\n"
                + "                  <epic:MaxNumberOfMailings>-1</epic:MaxNumberOfMailings>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:ModifyDate>2024-12-01T00:00:00</epic:ModifyDate>\r\n"
                + "                  <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "                  <epic:ModifyStoreNumber>2203</epic:ModifyStoreNumber>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:ModifyUser/>\r\n"
                + "                  <!--Always 0-->\r\n"
                + "                  <epic:NumberOfMailings>0</epic:NumberOfMailings>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:PrimaryAddressDate>2024-12-01T00:00:00</epic:PrimaryAddressDate>\r\n"
                + "                  <!--Always -1-->\r\n"
                + "                  <epic:PrimaryAddressID>-1</epic:PrimaryAddressID>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:PrimaryEmailDate>2024-12-01T00:00:00</epic:PrimaryEmailDate>\r\n"
                + "                  <!--Always -1:-->\r\n"
                + "                  <epic:PrimaryEmailID>-1</epic:PrimaryEmailID>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:PrimaryPhoneDate>2024-12-01T00:00:00</epic:PrimaryPhoneDate>\r\n"
                + "                  <!--Always -1:-->\r\n"
                + "                  <epic:PrimaryPhoneID>-1</epic:PrimaryPhoneID>\r\n"
                + "                  <!--Always Added-->\r\n"
                + "                  <epic:RecordState>Added</epic:RecordState>\r\n"
                + "                  <!--Always 9999-->\r\n"
                + "                  <epic:SalesAssociateNumber>9999</epic:SalesAssociateNumber>\r\n"
                + "                  <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "                  <epic:StoreNumber>2203</epic:StoreNumber>\r\n"
                + "               </epic:CustomerDivisionWCF>\r\n"
                + "            </epic:CustomerDivisions>\r\n"
                + "            <!--Always 0-->\r\n"
                + "            <epic:DistributionStatusCode>0</epic:DistributionStatusCode>\r\n"
                + "            <!--FirstName-->\r\n"
                + "            <epic:FirstName>" + name + "</epic:FirstName>\r\n"
                + "            <!--Gender-->\r\n"
                + "            <epic:Gender>" + gender + "</epic:Gender>\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:ID>-1</epic:ID>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:JobTitle/>\r\n"
                + "            <!--Always SPA-->\r\n"
                + "            <epic:LanguageCode>SPA</epic:LanguageCode>\r\n"
                + "            <!--Current date and time:-->\r\n"
                + "            <epic:LastMailingDate>2020-10-23T00:00:00</epic:LastMailingDate>\r\n"
                + "            <!--LastName-->\r\n"
                + "            <epic:LastName>" + lastName + "</epic:LastName>\r\n"
                + "            <!--Current date and time:-->\r\n"
                + "            <epic:LastUpdateDate>2020-10-23T00:00:00</epic:LastUpdateDate>\r\n"
                + "            <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "            <epic:LastUpdateStoreNumber>2203</epic:LastUpdateStoreNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:LastUpdateUser/>\r\n"
                + "            <!--MaritalStatus S or M-->\r\n"
                + "            <epic:MaritalStatus>S</epic:MaritalStatus>\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:NextAddressID>-1</epic:NextAddressID>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:Number/>\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:NumberMailings>-1</epic:NumberMailings>\r\n"
                + "            <!--Always 1-->\r\n"
                + "            <epic:OriginalDivisionID>1</epic:OriginalDivisionID>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:PhoneticKey/>\r\n"
                + "            <!--Always Added-->\r\n"
                + "            <epic:RecordState>Added</epic:RecordState>\r\n"
                + "            <!--Always -1-->\r\n"
                + "            <epic:SalesAssociateNumber>-1</epic:SalesAssociateNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:Salutation/>\r\n"
                + "            <!--Always A-->\r\n"
                + "            <epic:StatusCode>A</epic:StatusCode>\r\n"
                + "            <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "            <epic:StoreNumber>2203</epic:StoreNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:TitleCode/>\r\n"
                + "         </ns:customer>\r\n"
                + "         <!--Optional:-->\r\n"
                + "         <ns:customerEmails xmlns:b=\"http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
                + "                <!--Optional:-->\r\n"
                + "            <epic:CustomerNumber>-1</epic:CustomerNumber>\r\n"
                + "            <!--Optional:-->\r\n"
                + "            <epic:Items>\r\n"
                + "                    <!--Zero or more repetitions:-->\r\n"
                + "               <epic:CustomerEmailWCF>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:CreateDate>2020-10-23T00:00:00</epic:CreateDate>\r\n"
                + "                  <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "                  <epic:CreateStoreNumber>2203</epic:CreateStoreNumber>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:CreateUser i:nil=\"true\"/>\r\n"
                + "                  <!--Current date and time:-->\r\n"
                + "                  <epic:DateLastModified>2020-10-23T00:00:00</epic:DateLastModified>\r\n"
                + "                  <!--EmailAddress-->\r\n"
                + "                  <epic:EmailAddress>" + email + "</epic:EmailAddress>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <!--always 9-->\r\n"
                + "                  <epic:EmailIndicatorID>9</epic:EmailIndicatorID>\r\n"
                + "                  <!--always PERS-->\r\n"
                + "                  <epic:EmailTypeCode>PERS</epic:EmailTypeCode>\r\n"
                + "                  <!--always -1-->\r\n"
                + "                  <epic:ID>-1</epic:ID>\r\n"
                + "                  <!--Valid Store, QA=9010, PRD=2203-->\r\n"
                + "                  <epic:ModifyStoreNumber>2203</epic:ModifyStoreNumber>\r\n"
                + "                  <!--Optional:-->\r\n"
                + "                  <epic:ModifyUser i:nil=\"true\"/>\r\n"
                + "                  <!--always Added-->\r\n"
                + "                  <epic:RecordState>Added</epic:RecordState>\r\n"
                + "               </epic:CustomerEmailWCF>\r\n"
                + "            </epic:Items>\r\n"
                + "         </ns:customerEmails>\r\n"
                + "         <!--Optional:-->\r\n"
                + "      </ns:SaveNewCustomer>\r\n"
                + "   </soapenv:Body>\r\n"
                + "</soapenv:Envelope>";
    }
}
