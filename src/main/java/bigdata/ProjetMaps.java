package bigdata;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.apache.spark.input.PortableDataStream;

import scala.Tuple2;


public class ProjetMaps {

	static final String SUBPATH = "dem3/";
	static final int NAME_SIZE = 7;
	
	public static void main(String[] args) throws IOException {

		String testFiles = "hdfs://ripoux:9000/user/raw_data/dem3/N29W113.hgt,hdfs://ripoux:9000/user/raw_data/dem3/N41W112.hgt,hdfs://ripoux:9000/user/raw_data/dem3/N16W062.hgt"; 

		SparkConf conf = new SparkConf().setAppName("Projet Maps");
		JavaSparkContext context = new JavaSparkContext(conf);
		JavaPairRDD<String, PortableDataStream> streamRDD = context.binaryFiles(testFiles);
		List<Tuple2<String, PortableDataStream>> hgtData = streamRDD.collect();

	
		PngGenerator generator = new PngGenerator();
		
		for (Tuple2<String, PortableDataStream> hgtFile : hgtData) {
	
			ShortBuffer sb = null;	

			ByteBuffer bb = ByteBuffer.allocateDirect(hgtFile._2.toArray().length);
			bb.put(hgtFile._2.toArray());
			bb.flip();
			sb = bb.order(ByteOrder.BIG_ENDIAN).asShortBuffer();

			
			generator.setSb(sb);
			generator.generateWithImageGradient("/images/gradient.png");
			
			String fileName = hgtFile._1.substring(hgtFile._1.indexOf(SUBPATH)+SUBPATH.length(), (hgtFile._1.indexOf(SUBPATH)+SUBPATH.length())+NAME_SIZE).toUpperCase();
			
			generator.writePng(fileName);

	    	
	    }


		context.close();
	}

}
