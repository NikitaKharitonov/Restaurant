import ru.restaurant.Database;
import ru.restaurant.model.Customer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws SQLException, JAXBException {
        Customer customer = Database.getCustomerByCustomerId(1);
        System.out.println(customer);
        JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(customer, System.out);
    }
}
