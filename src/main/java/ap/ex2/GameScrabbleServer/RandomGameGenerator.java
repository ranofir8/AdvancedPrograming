package ap.ex2.GameScrabbleServer;

import ap.ex2.bookscrabble.model.host.GameSave;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;
import java.util.Random;
import java.util.List;

public class RandomGameGenerator implements org.hibernate.id.IdentifierGenerator {
    private final Random rand;

    public RandomGameGenerator() {
        this.rand = new Random();
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sesh, Object o) throws HibernateException {
        int randID;
        synchronized (this) {
            int result;
            do {
                randID = this.rand.nextInt(10000);
                Query q = sesh.createQuery("FROM ap.ex2.bookscrabble.model.host.GameSave E WHERE gameID = :randID");
                q.setParameter("randID", randID); //

                result = q.list().size();
            } while (result > 0);
        }
        return randID;
    }
}
