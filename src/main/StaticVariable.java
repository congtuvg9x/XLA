package main;

import java.util.ArrayList;
import java.util.List;

public class StaticVariable {
	// DUONG DAN DEN THU MUC NGUON
	public static String PATH = "/home/hanhdam442/workspace/XLA/";
	
	// DUONG DAN LUU ANH TRUY VAN
	public static String INPUT_QUERY = PATH + "WebContent/images/query.jpg";
	
	// DUONG DAN LUU SIFT CUA ANH TRUY VAN (SIFT MATCHING)
	public static String OUTPUT_SIFT = PATH + "query/query.sift";
	
	// DU LIEU CSDL SIFT VA CSDL ANH
	public static String DATA_SIFT = PATH + "data/sift/";
	public static String DATA_IMAGE = PATH + "WebContent/img/";
	
	// DU LIEU ANH VA SIFT KIEM THU
	public static String TEST_IMAGE = PATH + "data/test/";
	public static String TEST_SIFT = PATH + "data/test_sift/";
	
	// DU LIEU ANH TRUY VAN COSINE
	public static String INPUT_QUERY_COSINE = PATH + "WebContent/images/query_cosine.jpg";
	public static String OUTPUT_SIFT_COSINE = PATH + "query/sift/";
	public static String OUTPUT_CREATE_TABLE = PATH + "query/query_table";
	
	// SO KET QUA TRA VE TRONG 1 TRANG
	public static int offsetPage = 15;

	// LUU KET QUA TIM KIEM ANH THEO DO TUONG DONG COSINE
	public static List<Double> consine_list;
	public static List<String> nameTypeCosine;
	public static List<String> name_image;
	
	// LUU KET QUA TIM KIEM ANH THEO (SIFT MATCHING)
	public static List<String> nameImgs;
	public static List<Integer> euclide;
	
}
