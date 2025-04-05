package org.rococo.tests.data.tpl;

import com.atomikos.icatch.jta.UserTransactionManager;
import jakarta.transaction.UserTransaction;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class XaTransactionTemplate {

    private static final ThreadLocal<UserTransaction> TRANSACTION_HOLDER = new ThreadLocal<>();
    private final JdbcConnectionHolders holders;

    public XaTransactionTemplate(String... jdbcUrl) {
        this.holders = Connections.holders(jdbcUrl);
    }

    @SafeVarargs
    @Nullable
    public final <T> T execute(Supplier<T>... actions) {
        UserTransaction ut = TRANSACTION_HOLDER.get();
        boolean newTransaction = (ut == null);

        if (newTransaction) {
            ut = new UserTransactionManager();
            TRANSACTION_HOLDER.set(ut);
        }

        try {
            if (newTransaction) ut.begin();
            T result = null;
            for (Supplier<T> action : actions) {
                result = action.get();
            }
            if (newTransaction) ut.commit();
            return result;
        } catch (Exception e) {
            if (newTransaction) {
                try {
                    ut.rollback();
                } catch (Exception rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (newTransaction) TRANSACTION_HOLDER.remove();
            holders.close();
        }
    }
}
