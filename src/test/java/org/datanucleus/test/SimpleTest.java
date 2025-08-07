package org.datanucleus.test;

import java.util.*;
import org.junit.*;
import javax.jdo.*;

import static org.junit.Assert.*;
import mydomain.model.*;
import org.datanucleus.util.NucleusLogger;

public class SimpleTest
{
    @Test
    public void testSimple()
    {
        NucleusLogger.GENERAL.info(">> test START");
        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("MyTest");

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // [INSERT code here to persist object required for testing]
            Person person = new Person(1L, "John Doe");
            Person person2 = new Person(2L, "Jane Doe");
            pm.makePersistent(person);
            pm.makePersistent(person2);
            Address address = new Address(1L, "123 Main St", person);
            pm.makePersistent(address);
            Address address2 = new Address(2L, "456 Main St", person2);
            pm.makePersistent(address2);
            Address address3 = new Address(3L, "789 Main St", person);
            pm.makePersistent(address3);
            Address address4 = new Address(4L, "101 Main St", person2);
            pm.makePersistent(address4);
            tx.commit();
            pm.close();
            pm = pmf.getPersistenceManager();
            try (Query<?> q = pm.newQuery("JPQL", "SELECT distinct a.person FROM mydomain.model.Address a")) {
                pm.evictAll();
                List<?> results = (List<?>) q.execute();
                assertEquals("Number of addresses for person is wrong", 2, results.size());
                for (Object obj : results)
                {
                    Person addr = (Person) obj;
                    NucleusLogger.GENERAL.info("Address: id=" + addr.getId() + " person=" + addr.getName());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        catch (Throwable thr)
        {
            NucleusLogger.GENERAL.error(">> Exception in test", thr);
            fail("Failed test : " + thr.getMessage());
        }
        finally 
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        pmf.close();
        NucleusLogger.GENERAL.info(">> test END");
    }
}
