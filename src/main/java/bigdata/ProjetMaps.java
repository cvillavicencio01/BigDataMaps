package bigdata;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.input.PortableDataStream;

import scala.Tuple2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;


public class ProjetMaps {

	static final String SUBPATH = "dem3/";
	static final int NAME_SIZE = 7;
	private static final byte[] FAMILY = Bytes.toBytes("hgt_data");
	private static final byte[] ROW    = Bytes.toBytes("image");
	private static final byte[] TABLE_NAME = Bytes.toBytes("villavicencio");

	/**
	 * Clean the table if already one exists
	 * @param admin name of administrator
	 * @param table Contains the details of an HBase table
	 * @return void
	 */
	public static void createOrOverwrite(Admin admin, HTableDescriptor table) throws IOException {
		if (admin.tableExists(table.getTableName())) {
			admin.disableTable(table.getTableName());
			admin.deleteTable(table.getTableName());
		}
		admin.createTable(table);
	}

	/**
	 * Creates an HBase table
	 * @param connection Connection
	 * @return void
	 */
	public static void createTable(Connection connect) {
		try {
			final Admin admin = connect.getAdmin(); 
			HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
			HColumnDescriptor famLoc = new HColumnDescriptor(FAMILY); 
			tableDescriptor.addFamily(famLoc);
			createOrOverwrite(admin, tableDescriptor);
			admin.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Generates an Array containing all the coordinates for our htg files
	 * @return ArrayList<String>
	 */
	public static ArrayList<String>  getAllCoordinates() {
		
		ArrayList<String> coordinates = new ArrayList<String>();

		for(int x = -180; x <=179; x++) {

			for(int y = -89; y<=90; y++) {

				if (y <= 0 && x < 0)

					coordinates.add(String.format("N%02dW%03d", -1*y,-1*x));

				else if (y <= 0 && x >= 0)

					coordinates.add(String.format("N%02dE%03d", -1*y,x));

				else if (y > 0 && x < 0)

					coordinates.add(String.format("S%02dW%03d", y,-1*x));

				else if (y > 0 && x >= 0)

					coordinates.add(String.format("S%02dE%03d", y,x));

			}

		}
		
		return coordinates;
	}
	
	
	public static Connection getHBaseConnection() {
		Configuration hconf = HBaseConfiguration.create();
		Connection connec = null;
		try {
			connec = ConnectionFactory.createConnection(hconf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connec;
	}
	
	public static void insertImage(Table hbaseTable, String fileName, byte[] data) {
		Put put = new Put(Bytes.toBytes(fileName));
		put.addColumn(FAMILY, ROW, data);

		try {
			hbaseTable.put(put);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ShortBuffer hgtToBuffer(Tuple2<String, PortableDataStream> hgtFile) {
		
		ShortBuffer sb = null;	

		ByteBuffer bb = ByteBuffer.allocateDirect(hgtFile._2.toArray().length);
		bb.put(hgtFile._2.toArray());
		bb.flip();
		sb = bb.order(ByteOrder.BIG_ENDIAN).asShortBuffer();
		
		return sb;
	}

	public static void main(String[] args) throws IOException {

		String hadoopHome  = "hdfs://young:9000";
		String testFiles = hadoopHome+"/user/raw_data/dem3/";

		SparkConf conf = new SparkConf().setAppName("Projet Maps");
		JavaSparkContext context = new JavaSparkContext(conf);
		JavaPairRDD<String, PortableDataStream> streamRDD = context.binaryFiles(testFiles);

		createTable(getHBaseConnection());

		streamRDD.foreachPartition(streamHBaseRDD -> { 

			Table table = getHBaseConnection().getTable(TableName.valueOf(TABLE_NAME));

			PngGenerator generator = new PngGenerator();

			streamHBaseRDD.forEachRemaining(hgtFile -> {

				generator.setSb(hgtToBuffer(hgtFile));
				generator.generateImageWithGradient("/images/gradient.png");

				String fileName = hgtFile._1.substring(
						hgtFile._1.indexOf(SUBPATH)+SUBPATH.length(), 
						(hgtFile._1.indexOf(SUBPATH)+SUBPATH.length())+NAME_SIZE)
						.toUpperCase();


				insertImage(table, fileName, generator.getBytes());

			});

			table.close();
		});

		context.close();
	}
}
