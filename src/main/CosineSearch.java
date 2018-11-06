package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class CosineSearch
 */
@WebServlet("/SearchCosine")
public class CosineSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CosineSearch() {
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
		
		int numResult = StaticVariable.consine_list.size();
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
		
		List<String> name_image = new ArrayList<String>();
		List<Double> consine_list = new ArrayList<Double>();
		
		for(int i = begin ; i < end ; i++){
			name_image.add(StaticVariable.name_image.get(i));
			consine_list.add(StaticVariable.consine_list.get(i));
		}
		
		ServletContext applicationObject = getServletConfig().getServletContext();
		applicationObject.setAttribute("cosine", consine_list);
		applicationObject.setAttribute("name_image", name_image);
		
		response.sendRedirect("http://localhost:8080/XLA/searchCosine.jsp?page=" + page + "&numPage=" + numPage);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// Save query image into directory
		uploadDocument(request, response); // ANH TRUY VAN
		
		// Create SIFT file for query image
		createSIFT_ImageQuery();
		
		// Use Kmeans in created file
		createTableQuery();
		
		// Tinh Cosine
		calculatorCosine(response);
		
		
	}
	
	/**
	 * HAM TINH DO TUONG DONG COSINE
	 * @param response
	 * @throws IOException
	 */
	public void calculatorCosine(HttpServletResponse response) throws IOException {
		List<List<Integer>> visual_words = read_File(new File(StaticVariable.PATH + "data/F.txt"));
		List<List<Integer>> queries = read_File(new File(StaticVariable.PATH + "query/query_out"));	
		// Lay ten image
		List<String> name_image = read_name_image(new File(StaticVariable.PATH + "data/F.txt.list"));
		
        if(name_image == null || visual_words == null || queries == null){
        	response.sendRedirect("http://localhost:8080/XLA/error.jsp");
        	return;
        }
        
		// Tinh cosine cua query_out voi moi dong trong F.txt
		List<Integer> query = queries.get(0);

		int soluong = 0;

		List<Integer> visual_word = new ArrayList<Integer>();
		double tu, mau_visual, mau_query;
		float mau;
		double cosine;
		int len_visual = visual_words.size();
		
		ServletContext applicationObject = getServletConfig().getServletContext();
		List<Double> consine_list = new ArrayList<Double>();
		
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
			if (mau == 0) cosine = 0;
			else cosine = tu / mau;

			consine_list.add(cosine);
			
			if(cosine != 0){
				soluong++;
			}
			
		}
		
		System.out.println("TONG SO ANH KQ: " + consine_list.size());
		
		// Sap xep cosine --------------------------------------------
		int low = 0, high = consine_list.size()-1;
		sortCosine(consine_list, name_image, low, high);
		
		
		for (int i = 0; i < 10; i++) {
			System.out.println(consine_list.get(i));
		}
		StaticVariable.consine_list = consine_list;
		StaticVariable.name_image = name_image;
		
		// TINH TONG SO TRANG
		int numResult = consine_list.size();
		int numPage = numResult / StaticVariable.offsetPage;  System.out.println("numPage = " + numPage);
		float fnumPage = (float) numResult / StaticVariable.offsetPage; System.out.println("fnumPage = " + fnumPage);
		if(numPage < fnumPage) numPage = numPage + 1;
		
		applicationObject.setAttribute("cosine", consine_list);
		applicationObject.setAttribute("name_image", name_image);
		
		response.sendRedirect("http://localhost:8080/XLA/searchCosine.jsp?page=1&numPage=" + numPage);
		
	}
	
	/**
	 * SAP XEP GIA TRI COSINE GIAM DAN
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
		double pivot = Double.valueOf(cosine.get(middle));
 
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
 
		// recursively sort two sub parts
		if (low < j)
			sortCosine(cosine, name, low, j);
 
		if (high > i)
			sortCosine(cosine, name, i, high);
		
	}
	
	/**
	 * LAY TEN CUA ANH TRONG DANH SACH F.TXT
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
        
		br.readLine();
		
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
        StaticVariable.nameTypeCosine = nameTypeCosine;
        
        return name_image;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * DOC NOI DUNG FILE
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private List<List<Integer>> read_File(File file) throws IOException{
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
	        
		} catch(Exception e){
			return null;
		}
		
	}
	
	/**
	 * TAO SIFT CHO ANH TRUY VAN
	 * @throws IOException
	 */
	protected void createSIFT_ImageQuery() throws IOException {
		String cmd = "sh " + StaticVariable.PATH + "code_sh/image2sift.sh " 
					+  StaticVariable.INPUT_QUERY_COSINE + " "
				    +  StaticVariable.OUTPUT_SIFT_COSINE + "query_cosine.sift";
		System.out.println(cmd);
		Process p = Runtime.getRuntime().exec(cmd);

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null){
		    System.out.println(line);
		}
	}
	
	/**
	 * UPLOAD ANH TRUY VAN VAO THU MUC WEB
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
                //Xử lý danh sách các file đã được chọn
                for(FileItem item : items){
                	//String filename = new File(item.getName()).getName(); 
                	item.write(new File(StaticVariable.INPUT_QUERY_COSINE));
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
	
	/**
	 * TAO VECTOR CHO ANH TRUY VAN
	 * @throws IOException
	 */
	protected void createTableQuery() throws IOException {
		
		String cmd = "./" + StaticVariable.PATH + "code_sh/create-table.sh " + StaticVariable.PATH + "query/sift " 
				+ "./" + StaticVariable.PATH + "code_sh/assign " + StaticVariable.PATH + "/data/kmeans "
				+ StaticVariable.PATH + "query/query_out";
		
		// vocabularies kmeans
		System.out.println(cmd);
		Process p = Runtime.getRuntime().exec(cmd);
	
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null){
			System.out.println(line);
		}
	}

}
