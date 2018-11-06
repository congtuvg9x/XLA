package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import vn.hus.nlp.utils.FileIterator;
import vn.hus.nlp.utils.TextFileFilter;

/**
 * Servlet implementation class CompareSiftSearch
 */
@WebServlet("/CompareSiftSearch")
public class CompareSiftSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CompareSiftSearch() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String input = request.getParameter("input");
		int page = Integer.parseInt(request.getParameter("page"));
		int numPage = Integer.parseInt(request.getParameter("numPage"));
		
		String press = request.getParameter("press");
		System.out.println("press: " + press);	
		switch(press){
			case "Trang đầu":
				page = 1;
				break;
			case "Trang kế":
				if(page < numPage){
					page = page + 1;
				}
				break;
			case "Trang trước":
				if(page > 1){
					page = page - 1;
				}
				break;
			case "Trang cuối":
				page = numPage;
				break;
		}
		
		int numResult = StaticVariable.nameImgs.size();
		//int numPage = numResult / StaticVariable.offsetPage;  System.out.println("numPage = " + numPage);
		float fnumPage = (float) numResult / StaticVariable.offsetPage; System.out.println("fnumPage = " + fnumPage);
		if(numPage < fnumPage) numPage = numPage + 1;
		System.out.println("numPage = " + numPage);

		// TRA VE TOP K SCORE THEO TRANG
		int begin = (page - 1) * StaticVariable.offsetPage; // VI TRI TRANG BAT DAU
		int end = begin + StaticVariable.offsetPage;		// VI TRI KET THUC
		
		// KIEM TRA TRANG CO VUOT QUA KET QUA
		if( end > numResult ){
			end = numResult;
			// System.out.println("num result" + numResult + " end = " + end + " begin = " + begin);
		}
		
		List<String> nameImgs = new ArrayList<String>();
		List<Integer> euclide = new ArrayList<Integer>();
		
		for(int i = begin ; i < end ; i++){
			nameImgs.add(StaticVariable.nameImgs.get(i));
			euclide.add(StaticVariable.euclide.get(i));
		}
		
		ServletContext applicationObject = getServletConfig().getServletContext();
		//applicationObject.setAttribute("euclide", euclide);
		applicationObject.setAttribute("nameImgs", nameImgs);
		
		response.sendRedirect("http://localhost:8080/XLA/compare_sift.jsp?page=" + page + "&numPage=" + numPage);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// LUU FILE ANH TRUY VAN VAO THU MUC
		uploadDocument(request, response); // ANH TRUY VAN
		
		// TAO SIFT CHO ANH TRUY VAN
		createSIFT_ImageQuery(); 

		// TIM KIEM ANH TRONG CSDL
		queryFunction(response);

	}
	
	public void queryFunction(HttpServletResponse response) throws IOException{
        // DOC FILE SIFT TRUY VAN
        List<List<Integer>> querySift = read_Sift_File(new File(StaticVariable.OUTPUT_SIFT));
        
		List<List<Integer>> allSift = new ArrayList<>();
		List<String> nameImgs = new ArrayList<String>();
		List<Integer> euclide = new ArrayList<Integer>();
		List<String> nameType = new ArrayList<String>();
		String name = "";
		String[] types;
		int mark;
		int point;
		int k = 0;
		long startTime, endTime, duration;
		
		// LAY DUONG DANH THU MUC SIFT
        TextFileFilter fileFilter = new TextFileFilter(".sift");
        File inputDirFile = new File(StaticVariable.DATA_SIFT); 
        File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);
		
        if(inputFiles.length == 0 || querySift == null){
        	response.sendRedirect("http://localhost:8080/XLA/error.jsp");
        	return;
        }
        
        startTime = System.currentTimeMillis();
		// MOI 1 FILE SIFT THUC HIEN
        for (File file : inputFiles) {
        	//System.out.println(file.getAbsolutePath() + " " + file.getPath());
        	//types = file.getAbsolutePath().split("/");
        	//System.out.println(types[6] + ", " + types[7]);
        	
        	if(k == 1000) break; // TIM 1000 HINH DAU TIEN
        	
        	allSift = read_Sift_File(file);
        	point = compare2Sift(querySift, allSift);
        	
        	if(point != 0){
	        	euclide.add(point);
	        	nameImgs.add(file.getName());
	        	//nameType.add(types[7]);
        	}
        	
        	System.out.println("Tap tin: " + file.getName() + " " + (k++));
        }
		
        endTime = System.currentTimeMillis();
        duration = (long) (endTime - startTime) / 1000;
		System.out.println("THOI GIAN DOC FILE SIFT: " + duration);
		
		
		/*
		// SAP XEP KET QUA THEO POINT GIAM DAN
		for(int i = 0 ; i < euclide.size() ; i++){
			for(int j = i+1; j < euclide.size(); j++){
				if(euclide.get(i) < euclide.get(j)){
					mark = euclide.get(i); 			name = nameImgs.get(i);
					euclide.set(i, euclide.get(j)); nameImgs.set(i, nameImgs.get(j));
					euclide.set(j, mark);			nameImgs.set(j, name);
				}
			}
		}
		*/
		
		sortMatching(euclide, nameImgs, 0, euclide.size()-1);
		
		System.out.println("TONG SO ANH KQ " + euclide.size());
		
		
		// KET QUA POINT TRUY VAN VOI N ANH
		for(int i = 0; i < euclide.size() ; i++){
			System.out.println("IMG is: " + nameImgs.get(i) + " POINT = " + euclide.get(i));
			if(i == 15) break;
		}
		
		StaticVariable.nameImgs = nameImgs;
		StaticVariable.euclide = euclide;

		
		int numResult = nameImgs.size();
		int numPage = numResult / StaticVariable.offsetPage;  System.out.println("numPage = " + numPage);
		float fnumPage = (float) numResult / StaticVariable.offsetPage; System.out.println("fnumPage = " + fnumPage);
		if(numPage < fnumPage) numPage = numPage + 1;
		if (numResult > numPage && numPage == 0) numPage = 1;
		
        ServletContext applicationObject = getServletConfig().getServletContext();
        applicationObject.setAttribute("nameImgs", nameImgs);
        
        response.sendRedirect("http://localhost:8080/XLA/compare_sift.jsp?page=1&numPage=" + numPage);

	}

	/**
	 * TINH DIEM CHO n SIFT TRONG ANH TRUY VAN VA 1 ANH (n SIFT) TRONG CSDL
	 * @param query
	 * @param img
	 * @return
	 */
	private int compare2Sift(List<List<Integer>> query, List<List<Integer>> img){
		int numQuery = query.size();
		List<Integer> eachSift = new ArrayList<Integer>();
		List<Integer> eachQuery = new ArrayList<Integer>();
		int numImg = img.size();
		int count = 0;
		float min1, min2;
		float value = 0;
		
		//System.out.println("NUM QUERY " + numQuery);
		//System.out.println("NUM IMAGE " + numImg);
		
		// VOI MOI SIFT TRONG QUERY A
		for(int q = 0 ; q < numQuery ; q++){
			eachQuery = query.get(q);
			min1 = Float.MAX_VALUE;
			min2 = Float.MAX_VALUE;
					
			// MOI SIFT TRONG ANH B
			for(int i = 0 ; i < numImg ; i++){
				eachSift = img.get(i);
				value = 0;
				
				// LAY TUNG PHAN TU TRONG VECTOR
				for(int j = 0 ; j < eachQuery.size() ; j++){
					//System.out.println("query.get[j]: " + eachQuery.get(j) + " each: " + eachSift.get(j));
					value += (float) Math.abs(eachQuery.get(j) - eachSift.get(j))*Math.abs(eachQuery.get(j) - eachSift.get(j));
				}
				value = (float)Math.sqrt(value);
				// TIM GIA TRI NHO NHAT VA NHO THU 2
				if(value < min1){
					min2 = min1; 
					min1 = value; 
				}
				if(value < min2 && value > min1) min2 = value;
			}
			//System.out.println("Min1 = " + min1 + " min2 = " + min2);
			//if(q == 1) break;
			if((min1 / min2) < (float)0.8) count++;
		}
		
		return count;
	}
	
	
	private void sortMatching(List<Integer> matching, List<String> name, int low, int high) {
		if (matching == null || matching.size() == 0)
			return;
 
		if (low >= high)
			return;
 
		// pick the pivot
		int middle = low + (high - low) / 2;
		int pivot = matching.get(middle);
 
		// make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (matching.get(i) > pivot) {
				i++;
			}
 
			while (matching.get(j) < pivot) {
				j--;
			}
 
			if (i <= j) {
				// Hoan doi vi tri danh sach cosine
				Integer temp = matching.get(i);
				matching.set(i, matching.get(j));
				matching.set(j, temp);
					
				// Hoan doi vi tri danh sach ten
				String temp_name = name.get(i);
				name.set(i, name.get(j));
				name.set(j, temp_name);
				/*
				String type = nameType.get(i);
				nameType.set(i, nameType.get(j));
				nameType.set(j, type);
				*/
				i++;
				j--;
			}
		}
 
		// recursively sort two sub parts
		if (low < j)
			sortMatching(matching, name, low, j);
 
		if (high > i)
			sortMatching(matching, name, i, high);
		
	}
	
	
	/**
	 * DOC 1 FILE SIFT CUA CSDL
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private List<List<Integer>> read_Sift_File(File file){
		try{
			String line = "";
			int len = 0;
			String value[];
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	        	
			br.readLine();
			br.readLine();
			//int numFeatures = Integer.parseInt(br.readLine().trim()); 	// 128
			//int numSift = Integer.parseInt(br.readLine().trim());		// 721
			
			//MANG LUU TAT CA SIFT CUA ANH TRUY VAN
			List<Integer> eachSift;
			List<List<Integer>> allSift = new ArrayList<>();
			
			int k = 0;
	        while ((line = br.readLine()) != null) {
	        	value = line.split("\\s+");
	        	len = value.length;
	        	eachSift = new ArrayList<>();
	        	for(int i = 0 ; i < len ; i++){
	        		eachSift.add(Integer.parseInt(value[i]));
	        	}
	        	allSift.add(eachSift);	
	        }
	        
	        br.close();
	        fis.close();
	        
	        return allSift;
	        
		} catch(Exception e){ 
			return null;
		}
	}
	
	
	/**
	 * HIEN THI KET QUA FILE SIFT
	 * @param allSift
	 */
	private void showFileQuery(List<List<Integer>> allSift){
		List<Integer> eachSift = new ArrayList<Integer>();
		int allLen = allSift.size();
		
		for(int i = 0; i < allLen ; i++){
			eachSift = allSift.get(i);
			for(int j=0; j < eachSift.size(); j++){
				System.out.print(eachSift.get(j) + " ");
			}
			System.out.println("");
		}
	}

	/**
	 * TAO SIFT CHO ANH TRUY VAN
	 * @throws IOException
	 */
	protected void createSIFT_ImageQuery() throws IOException {
		
		String cmd = "sh " + StaticVariable.PATH + "code_sh/image2sift2.sh " 
					+  StaticVariable.INPUT_QUERY + " " +  StaticVariable.OUTPUT_SIFT + "";
		Process p = Runtime.getRuntime().exec(cmd);

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null){
		    System.out.println(line);
		}
		
	}
	
	/**
	 * UPLOAD ANH TRUY VAN
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	protected void uploadDocument(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean isMultipart= ServletFileUpload.isMultipartContent(request);
		
        if(isMultipart){
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            
            try {      
                List<FileItem> items= upload.parseRequest(request);
                //String contextPath = request.getContextPath(); //Lấy đường dẫn thư mục hiện hành /TKTT
                //Xử lý danh sách các file đã được chọn
                for(FileItem item : items){
                	//String filename = new File(item.getName()).getName(); 
                	item.write(new File(StaticVariable.INPUT_QUERY));
                } 
            } 
            catch (Exception e) {
                response.getWriter().print(e.getMessage());
            }
        }
        else{
            response.getWriter().print("Không thể upload");
        }
	}
	
	
	private List<List<Integer>> readFileQuery() throws IOException{
		try{
			// So sanh moi SIFT cua anh QUEY voi tat ca anh trong CSDL anh.
			File queryFile = new File(StaticVariable.OUTPUT_SIFT);
			String line = "";
			int len = 0;
			String value[];
			
			FileInputStream fis = new FileInputStream(queryFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	        
			int numFeatures = Integer.parseInt(br.readLine().trim()); 	// 128
			int numSift = Integer.parseInt(br.readLine().trim());		// 721
			
			//MANG LUU TAT CA SIFT CUA ANH TRUY VAN
			List<Integer> eachSift;
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
		
		}catch(Exception e){}
		return null;
		
		}	
}
