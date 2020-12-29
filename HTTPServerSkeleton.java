package httpserverskeleton;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Date;

public class HTTPServerSkeleton {

    static final int PORT = 6789;
    public static int threadCount = 0;
    public static PrintWriter log;
    public static void main(String[] args) throws IOException {

        int thread_id = 1;
        log = new PrintWriter(new FileOutputStream("log.txt"));
        ServerSocket serverConnect = new ServerSocket(PORT);
        log.println("Server Started");
        log.println("Listening for connections on port : " + PORT + " .... \n");
        //log.close();
        while(true)
        {
            Socket s=serverConnect.accept();
            WorkerThread worker = new WorkerThread(s, log, thread_id);
            Thread t = new Thread(worker);
            t.start();
            threadCount++;
            log.println("Connection established with " + thread_id + " No of worker thread : " + threadCount + "\n");
            log.flush();
            thread_id++;

        }
        
    }

}

class WorkerThread implements Runnable
{
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private PrintWriter log;

    //BufferedWriter writer;

    private final String HOME = "F:\\Class Materials\\L3T2\\CSE 322 (Computer Networks Sessional)\\Offline 15\\HTTP_project\\src\\httpserverskeleton\\";

    private int id = 0;
    private int errorCode = 0;

    public WorkerThread(Socket s,PrintWriter log, int id)
    {
        this.socket = s;

        try
        {
            this.is = this.socket.getInputStream();
            this.os = this.socket.getOutputStream();
            this.log = log;
            //writer = new BufferedWriter(new FileWriter("log.txt", true));
        }
        catch(Exception e)
        {
            System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
        }

        this.id = id;
    }

    public void run()
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(this.is));
        PrintWriter pr = new PrintWriter(this.os);
        BufferedOutputStream out = new BufferedOutputStream(this.os);



        log.println("Connection thread id is: " + this.id);
        log.flush();

        String request;
        String input;
        String path;
        String mimeType;

        String status_line_200 = "HTTP/1.1 200 OK";
        String server = "Server: Java HTTP Server";
        String date = "Date: " + new Date();
        String connection = "Connection: Closed";

        try{
            log.println("Incoming Request from Client... \n");
            log.flush();
            //log.append("Incoming Request from Client... \n");
            input = in.readLine();
            log.println(input);
            while(true){
                request = in.readLine();
                if(request == null || request.isEmpty() == true) break;
                log.println(request);
            }
            log.flush();


            if(input != null && input.startsWith("GET")){
                //System.out.println("GET Method");
                String [] parts = input.split(" ");

                path = parts[1].toLowerCase();
                System.out.println(path);

                if(path.equals("/")){
                    log.println("\n Request default index.html file");

                    String file_path = HOME + "index.html";

                    File file = new File(file_path);
                    int file_length = (int) file.length();

                    byte[] file_data = readFileData(file, file_length);
                    log.println("\n Sending Response...");
                    pr.println(status_line_200);
                    pr.println(server);
                    pr.println(date);
                    pr.println("Content-Length: " + file_length);
                    pr.println("Content-Type: text/html");
                    pr.println(connection);
                    pr.println();
                    pr.flush();
                    out.write(file_data, 0, file_length);
                    out.flush();
                    log.println("Responded Successfully\n");
                    log.flush();
                }
                else {

                    String file_path = HOME + construct_path(path);

                    System.out.println(file_path);

                    System.out.println(isFileExist(file_path));
                    if(!parts[2].equals("HTTP/1.1")){
                        log.println("400 Bad Request\n");
                        log.flush();
                        errorCode = 400;
                        file_path = HOME + "404.html";
                        File file = new File(file_path);
                        int file_length = (int) file.length();
                        byte[] file_data = readFileData(file, file_length);

                        pr.println("HTTP/1.1 400 Bad Request");
                        pr.println(server);
                        pr.println(date);
                        pr.println("Content-Length: " + file_length);
                        pr.println("Content-Type: text/html");
                        pr.println(connection);
                        pr.println();
                        pr.flush();

                        out.write(file_data, 0, file_length);
                        out.flush();

                    }
                    else if(isFileExist(file_path) == false) {
                        log.println("404 File Not Found");
                        log.flush();
                        errorCode = 404;
                        file_path = HOME + "404.html";
                        File file = new File(file_path);
                        int file_length = (int) file.length();
                        byte[] file_data = readFileData(file, file_length);

                        pr.println("HTTP/1.1 404 Not Found");
                        pr.println(server);
                        pr.println(date);
                        pr.println("Content-Length: " + file_length);
                        pr.println("Content-Type: text/html");
                        pr.println(connection);
                        pr.println();
                        pr.flush();

                        out.write(file_data, 0, file_length);
                        out.flush();

                    }

                    else {
                        mimeType = fileType(file_path);
                        //log.println("Requested File MIME Type: " + mimeType);
                        File file = new File(file_path);
                        int file_length = (int) file.length();

                        byte[] file_data = readFileData(file, file_length);
                        log.println("\n Sending Response...");
                        pr.println(status_line_200);
                        pr.println(server);
                        pr.println(date);
                        pr.println("Content-Length: " + file_length);
                        pr.println("Content-Type: " + mimeType);
                        pr.println(connection);
                        pr.println();
                        pr.flush();

                        out.write(file_data, 0, file_length);
                        out.flush();

                        log.println("Responded Successfully\n");
                        log.flush();
                    }

                }
            }
            else if(input != null && input.startsWith("POST")){
                System.out.println("POST Method");
                StringBuilder form_data = new StringBuilder();
                while(in.ready()){
                    form_data.append((char) in.read());
                }
                System.out.println(form_data.toString());
                String [] fields = form_data.toString().split("=");
                String form_input;
                if(fields.length == 1){
                    form_input = "";
                }
                else{
                    form_input = URLDecoder.decode(fields[1], "UTF-8");
                }
                log.println("Received Form Input : " + form_input);

                File response = new File(HOME + "\\blog\\new\\http_post.html");
//
//                StringBuilder contentBuilder = new StringBuilder();
//                try {
//                    BufferedReader fileRead = new BufferedReader(new FileReader(response));
//                    String str;
//                    while ((str = fileRead.readLine()) != null) {
//                        contentBuilder.append(str);
//                    }
//                    in.close();
//                } catch (IOException e) {
//                }
//                String content = contentBuilder.toString();
//
//                content = content.replace("Post->", "Post-> " + form_input);
//                System.out.println(content);
//                BufferedWriter bufwriter = new BufferedWriter(new FileWriter(HOME + "\\blog\\new\\http_post.html"));
//                bufwriter.write(content);//writes the edited string buffer to the new file
//                bufwriter.close();


                //File temp = new File("temp.html");
                int file_length = (int) response.length();

                byte[] file_data = readFileData(response, file_length);


                //byte[] file_data = content.getBytes();
                log.println("Sending POST Response....\n");
                pr.println("HTTP/1.1 200 OK");
                pr.println("Server: Java HTTP Server");
                pr.println("Date: " + new Date());
                pr.println("Content-Length: " + file_length);
                pr.println("Content-Type: text/html");
                pr.println("Connection: Closed");
                pr.println();
                //pr.flush();
                pr.println("<html>\n" +
                        "\t<head>\n" +
                        "\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                        "\t</head>\n" +
                        "\t<body>\n" +
                        "\t\t<h1> Welcome to CSE 322 Offline 1</h1>\n" +
                        "\t\t<h2> HTTP REQUEST TYPE-> POST </h2>\n" +
                        "\t\t<h2> Post-> "+form_input+"</h2>\n" +
                        "\t\t<form name=\"input\" action=\"http://localhost:6789/form_submited\" method=\"post\">\n" +
                        "\t\tYour Name: <input type=\"text\" name=\"user\">\n" +
                        "\t\t<input type=\"submit\" value=\"Submit\">\n" +
                        "\t\t</form>\n" +
                        "\n" +
                        "\t</body>\n" +
                        "</html>");
                pr.flush();
//                out.write(file_data,0, file_length);
//                out.flush();
                log.println("POST Response Sent Successfully");
                log.flush();


            }
        }

        catch (IOException e){
            System.out.println("IO exception;");
        }

        try
        {
            this.is.close();
            this.os.close();
            this.socket.close();
        }
        catch(Exception e)
        {

        }

        HTTPServerSkeleton.threadCount--;
        log.println("Client [" + id + "] is now terminating. No. of worker threads = "
                + HTTPServerSkeleton.threadCount);
        log.flush();

//        String str;
//
//        while(true)
//        {
//            try
//            {
//                if( (str = br.readLine()) != null )
//                {
//                    if(str.equals("BYE"))
//                    {
//                        System.out.println("[" + id + "] says: BYE. Worker thread will terminate now.");
//                        break; // terminate the loop; it will terminate the thread also
//                    }
//                    else if(str.equals("DL"))
//                    {
//                        try
//                        {
//                            File file = new File("capture.jpg");
//                            FileInputStream fis = new FileInputStream(file);
//                            BufferedInputStream bis = new BufferedInputStream(fis);
//                            OutputStream os = socket.getOutputStream();
//                            byte[] contents;
//                            long fileLength = file.length();
//                            pr.println(String.valueOf(fileLength));		//These two lines are used
//                            pr.flush();									//to send the file size in bytes.
//
//                            long current = 0;
//
//                            long start = System.nanoTime();
//                            while(current!=fileLength){
//                                int size = 10000;
//                                if(fileLength - current >= size)
//                                    current += size;
//                                else{
//                                    size = (int)(fileLength - current);
//                                    current = fileLength;
//                                }
//                                contents = new byte[size];
//                                bis.read(contents, 0, size);
//                                os.write(contents);
//                                //System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
//                            }
//                            os.flush();
//                            System.out.println("File sent successfully!");
//                        }
//                        catch(Exception e)
//                        {
//                            System.err.println("Could not transfer file.");
//                        }
//                        pr.println("Downloaded.");
//                        pr.flush();
//
//                    }
//                    else
//                    {
//                        System.out.println("[" + id + "] says: " + str);
//                        pr.println("Got it. You sent \"" + str + "\"");
//                        pr.flush();
//                    }
//                }
//                else
//                {
//                    System.out.println("[" + id + "] terminated connection. Worker thread will terminate now.");
//                    break;
//                }
//            }
//            catch(Exception e)
//            {
//                System.err.println("Problem in communicating with the client [" + id + "]. Terminating worker thread.");
//                break;
//            }
//        }
//
//        try
//        {
//            this.is.close();
//            this.os.close();
//            this.socket.close();
//        }
//        catch(Exception e)
//        {
//
//        }
//
//        TestServer.workerThreadCount--;
//        System.out.println("Client [" + id + "] is now terminating. No. of worker threads = "
//                + TestServer.workerThreadCount);
    }

    private String fileType(String path){
        System.out.println(path + " inside fileTYpe");
        if(path.endsWith("html")){
            return "text/html";
        }
        else if(path.endsWith("txt")){
            return "text/plain";
        }
        else if(path.endsWith("pdf")){
            return "application/pdf";
        }
        else if(path.endsWith("png")){
            return "image/png";
        }
        else if(path.endsWith("jpeg")){
            return "image/jpeg";
        }
        else if(path.endsWith("gif")){
            return "image/gif";
        }
        else if(path.endsWith("bmp")){
            return "image/bmp";
        }
        else if(path.endsWith("tiff")){
            return "image/tiff";
        }
        return "Unknown File Type";
    }

    private String construct_path(String path){
        String[] path_part = path.split("/");
        String constructed_path = "";

        for(int i = 0; i < path_part.length; i++){
            if(i == (path_part.length-1)) {
                constructed_path = constructed_path + path_part[i];
                break;
            }
            constructed_path = constructed_path + path_part[i] + "\\";
            //System.out.println(path);
        }
        return constructed_path;
    }

    private static boolean isFileExist(String path){
        File tmpDir = new File(path);
        return tmpDir.exists();
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }
}
