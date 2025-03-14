package co.miniso.rompefilas.service;

import co.miniso.rompefilas.db3.crm.ConsumerServices;
import co.miniso.rompefilas.db3.crm.exceptions.BadEmailException;
import co.miniso.rompefilas.db3.crm.exceptions.ExistEmailException;
import co.miniso.rompefilas.db3.model.Client;
import co.miniso.rompefilas.db3.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class ClientService {

    private static final String MODIFIED = "Modified";

    private static final String UNCHANGED = "Unchanged";

    private final ClientRepository clientRepository;

    private final ConsumerServices consumerServices;

    @Autowired
    public ClientService(ClientRepository clientRepository, ConsumerServices consumerServices) {
        this.clientRepository = clientRepository;
        this.consumerServices = consumerServices;
    }

    public Client getClientByDoc(String doc) {
        Optional<Client> client = clientRepository.findByDoc(doc);
        return client.orElse(null); // Retorna null si no encuentra el cliente
    }

    public String updateClient(Client oldClient, Client newClient) {
        int response = 500;
        try {
            if (!oldClient.getEmail().equals(newClient.getEmail())) {
                response = consumerServices.updateCustomer(newClient.getIdAptos(), newClient.getName(),
                        newClient.getGender(), newClient.getlastName(), newClient.getEmail(),
                        oldClient.getDocument(), newClient.getTypeId(), "111111101", UNCHANGED, MODIFIED);
                consumerServices.createUser(newClient.getName(), newClient.getlastName(), newClient.getGender(),
                        newClient.getEmail(), newClient.getTypeId(), newClient.getDocument());
            } else {
                response = consumerServices.updateCustomer(newClient.getIdAptos(), newClient.getName(),
                        newClient.getGender(), newClient.getlastName(), newClient.getEmail(),
                        oldClient.getDocument(), newClient.getTypeId(), newClient.getDocument(), UNCHANGED, UNCHANGED);
            }
            if (response == 200) {
                return "Se actualizaron correctamente los datos";
            } else {
                return "No se pudo actualizar el cliente, codigo de respuesta: " + response;
            }
        } catch (BadEmailException | IllegalStateException | IOException | ExistEmailException e1) {
            return e1.getMessage();
        }
    }
}
