package jp.co.intheforest;


import java.util.ArrayList;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.CqlRows;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.beans.Row;


public class CBUtility {

    static Cluster cluster;
    static Keyspace keyspace;
    static CassandraHostConfigurator chc;
    final static StringSerializer ss = StringSerializer.get();
    final static LongSerializer ls = LongSerializer.get();
    private String wcl;
    private String rcl;
    FastDateFormat df = FastDateFormat.getInstance("yyyy/MM/dd(E) HH:mm:ss");

    final static Logger logger = LoggerFactory.getLogger("CBUtility");

      
    CBUtility(String wConsistencyLevel, String rConsistencyLevel,String conHost){
        logger.debug("Cassandra Connection Start");
        wcl = wConsistencyLevel;
        rcl = rConsistencyLevel;
        try {

            chc = new CassandraHostConfigurator( conHost + ":9160");
            chc.setMaxActive(2);
            chc.setCassandraThriftSocketTimeout(10000);
            chc.setMaxWaitTimeWhenExhausted(4000);
            cluster = HFactory.getOrCreateCluster("Intheforest Cluster",chc);
            keyspace = HFactory.createKeyspace("CassandraBenchmark", cluster);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void releaseConnect() {
    }
    
    public void closeConnect() {
         cluster.getConnectionManager().shutdown();
    }
    
    public ArrayList<String> getKeys(){

        ArrayList<String> Idlist = new ArrayList<String>();
        CqlQuery<String,String,Long> cqlQuery = null;
        QueryResult<CqlRows<String,String,Long>> result = null;
        try {
            cqlQuery = new CqlQuery<String,String,Long>(keyspace, ss, ss, ls);
            cqlQuery.setQuery("select KEY from CBench limit 100");
            result = cqlQuery.execute();
            } catch (Exception e) {
                logger.error("error : " + e.getMessage()); 
        }

            if (result != null) {
            CqlRows<String, String,Long> rows = result.get();
            if (rows != null) {
                for(Row<String,String,Long> row : rows.getList()) {
                    if (row != null) {
                        Idlist.add(row.getKey());
                    }
                  }
             }
        }
            return Idlist;
    }
    
    public void getData(String key){
        int result_count=0;
        for (int i = 0; i < 10; i++) {
            CqlQuery<String,String,Long> cqlQuery = null;
            QueryResult<CqlRows<String,String,Long>> result = null;
            try {
                cqlQuery = new CqlQuery<String,String,Long>(keyspace, ss, ss, ls);
                String sql = "SELECT datastream" + i + " FROM CBench USING CONSISTENCY " + rcl 
                            + " WHERE KEY = '" + key + "'";
                logger.debug("sql :" + sql);
                cqlQuery.setQuery(sql);
                result = cqlQuery.execute();
            } catch (Exception e) {
            logger.error("Get Error.");
              }
       if (result != null) {
            CqlRows<String, String,Long> rows = result.get();
            if (rows != null) {
                for(Row<String,String,Long> row : rows.getList()) {
                    if (row != null) {
                        logger.debug("res :" + row.toString());
                    	result_count++;
                       }
                  }
             }
        }
       logger.info("get result count: {}",String.valueOf(result_count));
        }
    }

    public void putData(String data,String key){

        for (int i = 0; i < 10; i++) {
            CqlQuery<String,String,Long> cqlQuery = null;
            try {
                cqlQuery = new CqlQuery<String,String,Long>(keyspace, ss, ss, ls);
                String sql = "update CBench "
                            + "USING CONSISTENCY " + wcl 
                            + " set Tag" + i + " = 'data" + i + "', datastream" + i + " = '" + data
                            + "' WHERE KEY = '" + key + "'";
                cqlQuery.setQuery(sql);
                cqlQuery.execute();
            } catch (Exception e) {
                System.out.print("put Error!" + e.getMessage());
              }
         }
    }
    public void deleteData(String key){

        for (int i = 0; i < 10; i++) {
            CqlQuery<String,String,Long> cqlQuery = null;
            try {
                cqlQuery = new CqlQuery<String,String,Long>(keyspace, ss, ss, ls);
                String sql = "DELETE Tag" + i + " FROM CBench USING CONSISTENCY " + wcl 
                            + " WHERE KEY = '" + key + "'";
                cqlQuery.setQuery(sql);
                cqlQuery.execute();
                } catch (Exception e) {
                System.out.print("Delete Error!");
              }
        }
        CqlQuery<String,String,Long> cqlQuery = null;
        try {
            cqlQuery = new CqlQuery<String,String,Long>(keyspace, ss, ss, ls);
            String sql = "DELETE FROM CBench USING CONSISTENCY " + wcl 
                        + " WHERE KEY = '" + key + "'";
            cqlQuery.setQuery(sql);
            cqlQuery.execute();
            } catch (Exception e) {
            System.out.print("Delete Error!");
          }

    }
}
