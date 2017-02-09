package docker;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

public class ImageDockertoJson {
	static String imageName;
	static String lastPushed;
	static String imageShortDescription;
	static String dockerPullCommand;
	static String imageVesion;
	static String imageLinkGit;
	static String dockerRunCommand;
	static String linkWikipedia;
	static String linkDBpedia;
	static String dockerVersionSupported;
	static Document doc,doc2;

	public static int getResponseCode(String urlString) throws MalformedURLException, IOException {
		URL u = new URL(urlString);
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET");
		huc.connect();
		return huc.getResponseCode();
	}

	
	 private static String readAll(Reader rd) throws IOException {
		    StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }

		  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		    InputStream is = new URL(url).openStream();
		    try {
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		      String jsonText = readAll(rd);
		      JSONObject json = new JSONObject(jsonText);
		      return json;
		    } finally {
		      is.close();
		    }
		  }
	
		  private static String getUrlContents(String theUrl)
		  {
		    StringBuilder content = new StringBuilder();

		    // many of these calls can throw exceptions, so i've just
		    // wrapped them all in one try/catch statement.
		    try
		    {
		      // create a url object
		      URL url = new URL(theUrl);

		      // create a urlconnection object
		      URLConnection urlConnection = url.openConnection();

		      // wrap the urlconnection in a bufferedreader
		      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

		      String line;

		      // read from the urlconnection via the bufferedreader
		      while ((line = bufferedReader.readLine()) != null)
		      {
		        content.append(line + "\n");
		      }
		      bufferedReader.close();
		    }
		    catch(Exception e)
		    {
		      e.printStackTrace();
		    }
		    return content.toString();
		  }
		  
		  
	public static void main(String[] args) throws Throwable {
		Elements newsHeadlines,newsHeadlines2;
		Element newsHeadline;
		HttpResponse<JsonNode> response;
		int i = 0;
		int index = 0;
		String url;

		// ********** block column headers line CSV file ***** //

		String sFileMeta = "C:/docker/meta.json";//"/Users/BENAYED-PC/Desktop/docker/Test.csv";/////"Users/BENAYED-PC/Bureau/docker/Test.csv"
		//String sFileDockerFile = "C:/docker/DockerFiles.json";
		FileWriter writerMeta = new FileWriter(sFileMeta);
		//FileWriter writerDockerFile = new FileWriter(sFileDockerFile);
		
		
		//writerDockerFile.append("{");
		String dockerfiles="";
		for (i = 1; i <= 1; i++) {
			doc = Jsoup.connect("https://hub.docker.com/explore/?page=" + i).get();
			newsHeadlines = doc.select(".RepositoryListItem__flexible___3R0Sg");
			for (Element element : newsHeadlines) {
			String attr = element.select("a").first().attr("href");
		//String attr="/_/hello-world/";
				url = "https://hub.docker.com" + attr;
				String rdf = "";
				doc = Jsoup.connect(url).get();

				// 1. image name
				newsHeadline = doc.select("h2.RepositoryPageWrapper__repoTitle___3r12T a").get(0);
				try {
					imageName = newsHeadline.text();
					System.out.println(imageName);
				} catch (Exception e) {
					imageName = null;
				}
				  JSONObject json1 = readJsonFromUrl("https://api.microbadger.com/v1/images/"+imageName);
				  String chaine = json1.get("Versions").toString().substring(1,json1.get("Versions").toString().length()-1);
				  JSONObject json2= new JSONObject(chaine);		  
				  writerMeta.append("{\n\t");
				  chaine=chaine.replaceAll("\\{", "\\{\n\t");
				  chaine=chaine.replaceAll(",", ",\n\t");
				  chaine=chaine.replaceAll("\\[\\{", "\n\t\\[\\{\n\t");
				  chaine=chaine.replaceAll("\\}\\]", "\n\n\t\\}\\]");
				  chaine=chaine.replaceAll("\\}\\,", "\n\n\t\\}\\,");
				  writerMeta.append(chaine);
				  //writerMeta.append("\n");
				  //System.out.println(chaine2);
				  writerMeta.append(",");
				  writerMeta.append("\n\t");
				 // JSONArray jsonArray= new JSONArray(json2.get("Tags").toString());
				// 2. image tag
				 /* for (int k = 0; k < jsonArray.length(); k++) {      
				        JSONObject json3= new JSONObject(jsonArray.get(k).toString());
				       // System.out.println(json3.get("tag"));
				           
				}*/
				  
				  try {
						newsHeadlines = doc.select(
								"div.Card__block___1G9Iy div.Markdown__markdown___527C8 li a[href*=https://github.com/] ");

						org.jsoup.nodes.Attributes att;
						Elements eCodes;

						dockerfiles="\n\t"+dockerfiles+"\"dockerfiles\":";
						int k1=0;
							for (Element langelement : newsHeadlines) {
								k1++;
								dockerfiles=dockerfiles+"{\"versions\":"+"[";
								att = langelement.select("a[href*=https://github.com/]").get(0).attributes();

								eCodes = langelement.select("code");
								int k=0;
								for (Element eCode : eCodes) {
									k++;
									// il faut vérifier le dernier element
									dockerfiles=dockerfiles+eCode.text();
									if(k!=(eCodes.size()))
										dockerfiles=dockerfiles+",";
									//System.out.println(eCode.text());
								}
								dockerfiles=dockerfiles+"],";
								String s1=att.get("href");
								String s2=s1.replaceAll("/blob", "");
								String s3=s2.replaceAll("github.com", "raw.github.com");
								dockerfiles=dockerfiles+"\n\t\"DockerFileURL\": \""+s3+"\",";
								dockerfiles=dockerfiles+"\n\t\"DockerFileContent\": [";
								String output  = getUrlContents(s3);
								output=output.replaceAll("\n","@"); 
								//String[] someArray=output.split("(FROM)|(ADD)|(COPY)|(ENV)|(EXPOSE)|(LABEL)|(VOLUME)|(STOPSIGNAL)|(RUN)|(CMD)|(MAINTAINER)|(ENTRYPOINT)|(WORKDIR)|(USER)|(ARG)|(ONBUILD)|(HEALTHCHECK)|(SHELL)");
								String[] someArray=output.split("@@");
								 k=0;
								    for (int j = 0; j < someArray.length; j++) {
								    	someArray[j]=someArray[j].replaceAll("@",""); 
									//System.out.println(someArray[j]);
								    	dockerfiles=dockerfiles+"\""+someArray[j]+"\"";
								    	if(k!=(someArray.length-1))
								    		dockerfiles=dockerfiles+",\n\t";
								}
								
								    dockerfiles=dockerfiles+"]\n\t}";
								    
								    if(k1!=(newsHeadlines.size()))
								    	dockerfiles=dockerfiles+",\n\t";// il faut vérifier le dernier
							}

					} catch (Exception e) {
						
					}

				  
				  
				  
				  
				  //dockerfiles=dockerfiles+"}\n\t";  
				 // dockerfiles=dockerfiles+",\n\t";  // il faut vérifier le dernier
				  dockerfiles=dockerfiles+"\n";
dockerfiles=dockerfiles+"}";
				  
				  dockerfiles=dockerfiles.replaceAll("\\{", "\\{\n\t");
				 //dockerfiles=dockerfiles.replaceAll(",",",\n\t");
				  dockerfiles=dockerfiles.replaceAll("\\[\\{", "\n\t\\[\\{\n\t");
				  dockerfiles=dockerfiles.replaceAll("\\}\\]", "\n\n\t\\}\\]");
				  dockerfiles=dockerfiles.replaceAll("\\}\\,", "\n\n\t\\}\\,");
				  
				  writerMeta.append(dockerfiles);
			}
		} // For (all images)
				  
		writerMeta.flush();
		writerMeta.close();
	} // For (all images)

}
