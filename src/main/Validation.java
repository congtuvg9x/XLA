package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.jar.Attributes.Name;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.record.PrecisionRecord;

import vn.hus.nlp.utils.FileIterator;
import vn.hus.nlp.utils.TextFileFilter;

/**
 * Servlet implementation class Validation
 */
@WebServlet("/Validation")
public class Validation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Validation() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String cmd = "";
		
		Runtime.getRuntime().exec(new String[] {"sh", "-c", "mkdir " + StaticVariable.PATH + "/data/test"});
		Runtime.getRuntime().exec(new String[] {"sh", "-c", "mkdir " + StaticVariable.PATH + "/data/test_sift"});
		// XOA FILE
		Runtime.getRuntime().exec(new String[] {"sh", "-c", "rm " + StaticVariable.PATH + "/data/test/*.*"});
		Runtime.getRuntime().exec(new String[] {"sh", "-c", "rm " + StaticVariable.PATH + "data/test_sift/*.*"});
		
		List<String> numImage = randomImage(100);
		
		// COPY FILE
		for (int i = 0 ; i < numImage.size() ; i++){
			cmd = "cp " + StaticVariable.DATA_IMAGE + numImage.get(i) + " " + StaticVariable.TEST_IMAGE + numImage.get(i);
			try {
				Runtime.getRuntime().exec(cmd).waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// SIFT VA VECTOR CHO ANH KIEM THU 
		try {
			//Runtime.getRuntime().exec("./" + StaticVariable.PATH + "code_sh/test.sh").waitFor();
			cmd = "./" + StaticVariable.PATH + "code_sh/dir2sift.sh " 
						+ StaticVariable.PATH + "data/test " 
						+ StaticVariable.PATH + "data/test_sift";
			System.out.println(cmd);
			Runtime.getRuntime().exec(cmd).waitFor();
			
			cmd = "./" + StaticVariable.PATH + "code_sh/create-table.sh "
					   + StaticVariable.PATH + "data/test_sift " + 
				  "./" + StaticVariable.PATH + "code_sh/assign "
				       + StaticVariable.PATH + "data/kmeans "
				       + StaticVariable.PATH + "data/test_query_out ";
			System.out.println(cmd);
			Runtime.getRuntime().exec(cmd).waitFor();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("NAME = " + numImage.get(0));
		//System.out.println("size of numImage: " + numImage.size());
		
		//List<String> 
		numImage = getListFile();
		List<String> listTypes = getListTypes(numImage);
		
		calculatePrecision(response, listTypes); 
		
		response.sendRedirect("http://localhost:8080/XLA/validation.jsp");
	}

	/**
	 * LAY DANH SACH TEN CUA ANH KIEM THU
	 * @return
	 * @throws IOException
	 */
	public List<String> getListFile() throws IOException {
		try{
			List<String> nameImage = new ArrayList<String>();
			String line = "";
			int len;
			String value[];
			
			FileInputStream fis = new FileInputStream(new File(StaticVariable.PATH + "data/test_query_out.list"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	
			br.readLine();
			
	        while ((line = br.readLine()) != null) {
	        	value = line.split("/");
	        	String nameLine[] = value[4].split(".sift");
	        	nameImage.add(nameLine[0]);
	        }
			
			return nameImage;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * TAO NGAU NHIEN 100 ANH TEST
	 * @param num
	 * @return
	 */
	public List<String> randomImage(int num){
		List<String> numImage = new ArrayList<String>();
        TextFileFilter fileFilter = new TextFileFilter(".jpg");
        File inputDirFile = new File(StaticVariable.DATA_IMAGE); 
        File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);
        int max = inputFiles.length;

		while (numImage.size() < num){
			Random rn = new Random();
            int range = max - 1;
            int r = 1 + rn.nextInt(range);	
			File file = inputFiles[r];
			if(!numImage.contains(file.getName())){
				numImage.add(file.getName());
			}
		}
		
		return numImage;
	}
	
	/**
	 * LAY 'NHAN' CUA HINH ANH
	 * @param name
	 * @return
	 * @throws IOException
	 */
	private List<String> getListTypes(List<String> name) throws IOException{
		try{
		String line = "";
		int len;
		String value[];
		List<String> types = new ArrayList<String>();
		List<Integer> pos = new ArrayList<Integer>();
		
		FileInputStream fis = new FileInputStream(new File(StaticVariable.PATH + "data/F.txt.list"));
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        while ((line = br.readLine()) != null) {
        	value = line.split("/");
        	len = value.length;
        	String nameLine[] = value[len-1].split(".sift");
        	if(name.contains(nameLine[0])){
        		types.add(value[len-2]);
        		// TIM VI TRI
        		for(int i = 0; i<name.size(); i++){
        			if(name.get(i).equals(nameLine[0])){
        				pos.add(i);
        				break;
        			}
        		}
        	}	
        }
        
        // SAP XEP
		for(int i = 0 ; i < types.size() ; i++){
			for(int j = i+1; j < types.size(); j++){
				if(pos.get(i) > pos.get(j)){
					int p = pos.get(i); 			String t = types.get(i);
					pos.set(i, pos.get(j)); 		types.set(i, types.get(j));
					pos.set(j, p);					types.set(j, t);
				}
			}
		}
        
        return types;
        
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * HAM TINH TOAN PRECISION
	 * @param response
	 * @param listTypes
	 * @throws IOException
	 */
	public void calculatePrecision(HttpServletResponse response, List<String> listTypes) throws IOException {
		double tu, mau_visual, mau_query;
		float mau;
		double cosine;
		int vName = 0;
		int pos = 0;
		
		ServletContext applicationObject = getServletConfig().getServletContext();
		List<Double> precision = new ArrayList<Double>();
		List<List<Integer>> visual_words = read_Sift_File(new File(StaticVariable.PATH + "data/F.txt"));
		List<List<Integer>> queries = read_Sift_File(new File(StaticVariable.PATH + "data/test_query_out"));
		List<Integer> visual_word = new ArrayList<Integer>();

        if(visual_words == null || queries == null){
        	response.sendRedirect("http://localhost:8080/XLA/error.jsp");
        	return;
        }
        
		int len_visual = visual_words.size();
		System.out.println("Len visual = " + len_visual);
		
		float topK = 100;
		double sum = 0;
		int numTotal = 0;
		double sumrecall = 0;
		
		List<String> nameRecall = new ArrayList<String>();
		nameRecall.add("background");
		nameRecall.add("car");
		nameRecall.add("moto");
		nameRecall.add("plane");
		nameRecall.add("people");
		nameRecall.add("class1");
		nameRecall.add("class2");
		nameRecall.add("class3");

		List<Integer> valueRecall = new ArrayList<Integer>();
		valueRecall.add(900);
		valueRecall.add(1155);
		valueRecall.add(800);
		valueRecall.add(800);
		valueRecall.add(435);
		valueRecall.add(17);
		valueRecall.add(23);
		valueRecall.add(21);
		
		// VOI MOI ANH TEST
		for(int q = 0; q < queries.size(); q++){
			
			List<Integer> query = queries.get(q);
			List<Double> consine_list = new ArrayList<Double>();
			// Lay loai image
			List<String> nameTypeCosine = read_name_image(new File(StaticVariable.PATH + "data/F.txt.list"));
			
			for (int i = 0; i < len_visual; i++) {
				cosine = 0;
				tu = 0; mau_visual = 0; mau_query = 0;
				visual_word = visual_words.get(i);
				
				// Tinh cosin
				int len_vector = visual_word.size(); 
	
				for (int j = 0; j < len_vector; j++) { // j chay tu 0 den 1999
					tu += query.get(j) * visual_word.get(j);
					mau_query += query.get(j) * query.get(j);
					mau_visual += visual_word.get(j) * visual_word.get(j);
				}
				mau = (float) (Math.sqrt(mau_query) * Math.sqrt(mau_visual));
				if (mau == 0)	 cosine = 0;
				else cosine = tu / mau;
	
				consine_list.add(cosine);
			}

			pos++;
			
			// Sap xep cosine --------------------------------------------
			int low = 0, high = consine_list.size()-1;
			sortCosine(consine_list, nameTypeCosine, low, high);
	
			String rName = "";
			topK = 0;
			int count = 0;
			for (int i = 0; i < consine_list.size(); i++) {
				if (consine_list.get(i) > 0.25) {
					if (listTypes.get(q).equals(nameTypeCosine.get(i))) {
						rName = nameTypeCosine.get(i);
						count++;
					}
					topK++;
				}
				/*
				if (i < topK) {
					//System.out.println("Cosine: " + consine_list.get(i) + " Type Cosine:" + nameTypeCosine.get(i) + " Type:" + listTypes.get(q));
					if (listTypes.get(q).equals(nameTypeCosine.get(i))) {
						rName = nameTypeCosine.get(i);
						count++;
					}
				}*/
			}
			
			for(int j=0 ; j < nameRecall.size(); j++){
				if(nameRecall.get(j).equals(rName)){
					vName = valueRecall.get(j);
					break;
				}
			}
			
			if((double) count/topK != 0.0 && (double) count/vName != 0.0){
				sum += (double) count/topK;
				sumrecall += (double) count/vName;
				numTotal++;
				System.out.println(pos + ".Precision=" + Math.round((count/(double)topK)*100.00)/100.0 
						+ "\tRecall=" + Math.round((count/(double)vName)*100.00)/100.0);
			}
		}
		precision.add(sum/numTotal);
		precision.add(sumrecall/numTotal);
		
		System.out.println("AVG Precision = " + (sum/numTotal));
		System.out.println("AVG Recall = " + (sumrecall/numTotal));
		applicationObject.setAttribute("precision", precision);
	}
	
	/**
	 * SAP XEP
	 * @param cosine
	 * @param name
	 * @param low
	 * @param high
	 */
	private void sortCosine(List<Double> cosine, List<String> name, int low, int high) {
		if (cosine == null || cosine.size() == 0)
			return;
 
		if (low >= high)
			return;
 
		// pick the pivot
		int middle = low + (high - low) / 2;
		double pivot = cosine.get(middle);
 
		// make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (cosine.get(i) > pivot) {
				i++;
			}
 
			while (cosine.get(j) < pivot) {
				j--;
			}
 
			if (i <= j) {
				// Hoan doi vi tri danh sach cosine
				double temp = cosine.get(i);
				cosine.set(i, cosine.get(j));
				cosine.set(j, temp);
				
				// Hoan doi vi tri danh sach ten
				String temp_name = name.get(i);
				name.set(i, name.get(j));
				name.set(j, temp_name);
				
				i++;
				j--;
			}
		}
 
		if (low < j)
			sortCosine(cosine, name, low, j);
 
		if (high > i)
			sortCosine(cosine, name, i, high);
		
	}
	
	/**
	 * LAY NHAN CHO ANH
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private List<String> read_name_image(File file) throws IOException{
		try{
		String line = "";
		int len = 0;
		String value[];
		String valueAfter[];
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        
		br.readLine(); 	// 4151
		
		//MANG LUU TAT CA TEN CUA ANH TRONG CSDL
		List<String> name_image = new ArrayList<>();
		List<String> nameTypeCosine = new ArrayList<>();
		
		
        while ((line = br.readLine()) != null) {
        	value = line.split("/");
        	len = value.length;
        	valueAfter = value[len-1].split(".sift");
        	name_image.add(valueAfter[0]);
        	nameTypeCosine.add(value[len-2]);
        }
        
        //StaticVariable.nameTypeCosine = nameTypeCosine;
        
        return nameTypeCosine;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * DOC VECTOR
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private List<List<Integer>> read_Sift_File(File file) throws IOException{
		try{
		String line = "";
		int len = 0;
		String value[];
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        
		br.readLine(); 	// 128
		//int numSift = Integer.parseInt(br.readLine().trim());		// 721
		
		//MANG LUU TAT CA SIFT CUA ANH TRUY VAN
		List<Integer> eachSift = new ArrayList<>();
		List<List<Integer>> allSift = new ArrayList<>();
		
		
        while ((line = br.readLine()) != null) {
        	value = line.split(" ");
        	len = value.length;
        	eachSift = new ArrayList<>();
        	for(int i = 0 ; i < len ; i++){
        		eachSift.add(Integer.parseInt(value[i]));
        	}
        	allSift.add(eachSift);	
        }
        
        return allSift;
		}catch(Exception e){
			return null;
		}
		
	}
}