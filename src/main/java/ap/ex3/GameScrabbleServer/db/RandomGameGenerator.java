package ap.ex3.GameScrabbleServer.db;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;
import java.util.Random;

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
                @SuppressWarnings("JpaQlInspection") Query q = sesh.createQuery("FROM ap.ex3.GameScrabbleServer.Saves.GameSave E WHERE E.gameID = :randID");
                q.setParameter("randID", randID);

                result = q.list().size();
            } while (result > 0);
        }
        return randID;
    }
}
