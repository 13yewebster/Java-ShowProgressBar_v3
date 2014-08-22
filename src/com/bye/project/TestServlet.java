package com.bye.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 97987982739182731L;
//	public static final long MAX_UPLOAD_IN_MEGS = 50;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		out.println("Hello<br/>");

		boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
		if (!isMultipartContent) {
			out.println("You are not trying to upload<br/>");
			return;
		}
		out.println("You are trying to upload<br/>");

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
//		upload.setSizeMax(MAX_UPLOAD_IN_MEGS * 1024 * 1024);

		TestProgressListener testProgressListener = new TestProgressListener();
		upload.setProgressListener(testProgressListener);

		HttpSession session = request.getSession();
		session.setAttribute("testProgressListener", testProgressListener);

		String filePath = "/Users/ByeWebster/Desktop/TempFiles";
		try {
			FileItemIterator iter = upload.getItemIterator(request);
            FileItemStream item = null;
            String name = "";
            InputStream stream = null;
            while (iter.hasNext()){
               item = iter.next();
               name = item.getFieldName();
               stream = item.openStream();
              if(item.isFormField()){
              	out.write("Form field " + name + ": " + Streams.asString(stream) + "<br/>");
              	}
              else {
                    name = item.getName();
                    if(name != null && !"".equals(name)){
                       String fileName = new File(item.getName()).getName();
                       out.write("Client file: " + item.getName() + " <br/>with file name " + fileName + " was uploaded.<br/>");
                       File file = new File(filePath+"/"+fileName);
                       FileOutputStream fos = new FileOutputStream(file);
                       long fileSize = Streams.copy(stream, fos, true);
                       out.write("Size was " + fileSize + " bytes <br/>");
                       out.write("File Path is " + file.getPath() + "<br/>");
                    }
                 }
            }
		} catch (FileUploadException e) {
			out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
