import ru.restaurant.Database;
import ru.restaurant.model.Customer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws SQLException, JAXBException {
        Object a = 2.3;
        int b = (Integer) a;
    }
}
