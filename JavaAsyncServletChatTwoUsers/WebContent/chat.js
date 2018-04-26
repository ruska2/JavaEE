var xmlHttp;// global instance of XMLHttpRequest
function createXmlHttpRequest()
{
        xmlHttp=new XMLHttpRequest();
   
}

function startRequest()
{		
		console.log("start");
		createXmlHttpRequest();
		var meno = document.getElementsByName('meno')[0].value
		xmlHttp.open("GET","/JavaAsyncServletChatTwoUsers/asyncchat?meno=" + meno ,true);
		xmlHttp.onreadystatechange=handleStateChange;
		xmlHttp.send(null); 
} 

function handleStateChange()
{
    if (xmlHttp.readyState===4)
    {
        if(xmlHttp.status===200)
        {
        	 try{var msgs  =  
                 xmlHttp.responseXML
                        .getElementsByTagName("line");
                //[0].nodeValue;
                var allmsgs = "";

                for (i = 0; i < msgs.length; i++)
                    allmsgs = allmsgs + msgs[i].childNodes[0].nodeValue + "<br />";
                    
                document.getElementById("msgs").innerHTML=allmsgs;
                
                startRequest();
        	 }catch(e){
        		startRequest();
        	 }
        }
        else if (xmlHttp.status !== 0)  // the pending request of a page that we already left
        {
           alert("Error loading page "+ xmlHttp.status + ":"+xmlHttp.statusText);
        }
    }
   
}



function sendPost(){
	setTimeout(function(){
		var msg2 = document.getElementsByName('msg')[0].value
		var meno = document.getElementsByName('meno')[0].value
		
		$(document).ready(function () {
			$.ajax({
		        type: "POST",
		        url: "/JavaAsyncServletChatTwoUsers/chatservlet",
		        data:{"msg": msg2, "meno": meno},
		    });
		});
	},100);
}


