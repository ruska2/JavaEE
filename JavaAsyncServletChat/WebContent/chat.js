var xmlHttp;// global instance of XMLHttpRequest
function createXmlHttpRequest()
{
    if(window.ActiveXObject)
    {
        xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    else if(window.XMLHttpRequest)
    {
        xmlHttp=new XMLHttpRequest();
    }
}

function startRequest()
{		
		console.log("start");
		createXmlHttpRequest();
		  xmlHttp.open("GET","/JavaAsyncServletChat/asyncchat" ,true);
		  xmlHttp.onreadystatechange=handleStateChange;
		  xmlHttp.send(null);
} 

function handleStateChange()
{
    if (xmlHttp.readyState===4)
    {
        if(xmlHttp.status===200)
        {
          var msgs  =  
             xmlHttp.responseXML
                    .getElementsByTagName("line");
            //[0].nodeValue;
            var allmsgs = "";

            for (i = 0; i < msgs.length; i++)
                allmsgs = allmsgs + msgs[i].childNodes[0].nodeValue + "<br />";
                
            document.getElementById("msgs").innerHTML=allmsgs;

            startRequest();
        }
        else if (xmlHttp.status !== 0)  // the pending request of a page that we already left
        {
           alert("Error loading page "+ xmlHttp.status + ":"+xmlHttp.statusText);
        }
    }
} 


