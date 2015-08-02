/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    var totalblogwhenpageloaded;
    var oImg1="";
    var imsc1="";
    var postuser = "";
    var postmsg = "";
    var upperlimit=10;
    var lowerlimit=0;
   var previoustask= true;
   var dataavailable=true;
   var Cdata;
   var commentblogid;
   var userfeed;
   var username;
    
    $('.bxslider').bxSlider({
        auto: true,
        autoControls: true
    });

    var image = new Image();
    $.ajax({
        type: 'GET',
        url: 'rs/blog/getcurrentuserimage',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function (data) {

            console.log(data);
            $.each(data, function (index, item) {
                username=item.username;
                if (item.username==="") {

                }
                else {



                    var img = document.createElement("img");

                     oImg1 = document.createElement("img");
                     imsc1 = 'data:image/jpg;base64, ' + item.myimage;
                    oImg1.setAttribute('src', imsc1);
                    oImg1.setAttribute('width', 100);
                    oImg1.setAttribute('height', 20);
//document.body.appendChild(oImg);
                    $("#photo").append(oImg1);
                    //$("#name").append("<td>" + item.name + "</td>"); 
                }
            });

        },
        error: function (jqxhr, status, errorMsg) {
            //window.location.replace("http://blogsmaster-c0652113.rhcloud.com/");
           // alert('Failed! ' + errorMsg);
        }
    });
    
    
    function bringPost(up,lo){
        
               $("#loading").css("display", "block");

var limits={upper: up, lower: lo};
    $.ajax({
        type: "POST",
        url: "rs/blog/global",
        data: JSON.stringify(limits),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        //async:true,
        success: function (data) {
            console.log(data.length);

               $("#loading").css("display", "none");

                previoustask=true;
            $.each(data, function (index, item) {
   if(item.errorcode==="nodatafound")
{
    $("#nodata").fadeIn();
                   $("#loading").css("display", "none");

    //alert("no data found");
}
else{
            // var id=$(".photos").attr('id');
             var match ="photo" + item.blogid;//totalblogwhenpageloaded=item.totalblogs; 
                if(document.getElementById(match))
                {
                   //alert('matched'); 
                }
                else{
                var oImg = item.blogid;

                var img = document.createElement("img");
                
                oImg = document.createElement("img");
                var imsc = 'data:image/jpg;base64, ' + item.bloguserimage;
                oImg.setAttribute('src', imsc);
                oImg.setAttribute('height', 50);
                oImg.setAttribute('width', 50);
//document.body.appendChild(oImg);
                var photodata = $("<div id='photo" + item.blogid + "' class='photos'></div><div id='name" + item.blogid +"' class='name'></div><div id='blogs" + item.blogid + "' class='blogs'></div><br><br><input type='button' id="+item.blogid+" value='Share your views' class='comment'><br><br>");

                $('#mainblog').append(photodata);
                $("#photo" + item.blogid).append(oImg);
                $("#blogs" + item.blogid).append(item.blogtext);
                $("#name" + item.blogid).append(item.bloguser+" Said");



                oImg = "";
            }//$("#name").append("<td>" + item.name + "</td>"); 
                }
            });
        
        },
        error: function (jqxhr, status, errorMsg) {
            alert('Failed! ' + errorMsg);
        }
    });
}


/* bringing every post */

bringPost(upperlimit,lowerlimit);




    $("#postblog").click(function (e) {
        e.preventDefault();
        el = document.getElementById("blog");
        e2 = document.getElementById("opac");
        el.style.visibility = (el.style.visibility === "visible") ? "hidden" : "visible";
        e2.style.visibility = (e2.style.visibility === "visible") ? "hidden" : "visible";
    });

    $("#closebox").click(function () {
        el = document.getElementById("blog");
        e2 = document.getElementById("opac");
        el.style.visibility = (el.style.visibility === "visible") ? "hidden" : "visible";
        e2.style.visibility = (e2.style.visibility === "visible") ? "hidden" : "visible";
    });




    $("#post").click(function () {
       
     

        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("POST", "AshychronousPolling?t=" + new Date(), false);
        var post = escape(document.getElementById("blogarea").value);
        xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
           //var nameText = escape(document.getElementById("name").value);
        //var messageText = escape(document.getElementById("message").value);
        //document.getElementById("message").value = "";
              xmlhttp.send("blog=" + post);

        el = document.getElementById("blog");
        e2 = document.getElementById("opac");
        el.style.visibility = (el.style.visibility === "visible") ? "hidden" : "visible";
        e2.style.visibility = (e2.style.visibility === "visible") ? "hidden" : "visible";

    });


    function getMessages() {
        var messagesWaiting = false;
        if (!messagesWaiting) {
            messagesWaiting = true;
            $.ajax({
                url: "AshychronousPolling?t=" + new Date(),
                method: "get",
                dataType: 'json',
                cache: false,
                async: true,
                success: function (data) {
                    //alert(data);
                    //console.log(data);
                    $.each(data, function (index, item) {
                        if (postuser !== item.user || postmsg !== item.blog)
                        {
                            var oImg = item.blogid;

                            var img = document.createElement("img");

                            oImg = document.createElement("img");
                            var imsc = 'data:image/jpg;base64, ' + item.image;
                            oImg.setAttribute('src', imsc);
                            oImg.setAttribute('height', 50);
                            oImg.setAttribute('width', 50);
                            var photodata = $("<div id='photo" + item.blogid + "' class='photos'></div><div id='name" + item.blogid +"' class='name'></div><div id='blogs" + item.blogid + "' class='blogs'></div><br><br><input type='button' id="+item.blogid+" value='Share your views' class='comment'><br><br>");
                            $('#mainblog').prepend(photodata);
                            $("#photo" + item.blogid).append(oImg);
                            $("#blogs" + item.blogid).append(item.blog);
                            $("#name" + item.blogid).append(item.user+" Said");

                            //$("#content").append(value.blog);
                            postuser = item.user;
                            postmsg = item.blog;
                        }

                        messagesWaiting = false;


                    });
                }

            });
        }
    }


    getMessages();
    setInterval(getMessages, 3000);
    
    
    
//    $("#mainblog").on('click', '.comment', function(e){
//        var blogid=$(this).attr('id');
//        alert(blogid);
//         el = document.getElementById("commentarea");
//      //  e2 = document.getElementById("inner");
//       // el.style.visibility="hidden";
//                //e2.style.visibility="hidden";
//
//        el.style.visibility = (el.style.visibility === "visible") ? "hidden" : "visible";
//               // e2.style.visibility = (el.style.visibility === "visible") ? "hidden" : "visible";
//
//        $('#commentarea').css( 'top', e.pageY );
//    $('#commentarea').css( 'left', e.pageX );
//    // $('#inner').css( 'top', e.pageY );
//    //$('#inner').css( 'left', e.pageX+300 );
//    
//                       var  commentimg = document.createElement("img");
//commentimg.setAttribute('src', imsc1);
//                    commentimg.setAttribute('width', 100);
//                    commentimg.setAttribute('height', 20);
//                    $("#usimg").html(commentimg);
//                    
//            $.ajax({
//               url:"rs/blog/comments",
//               Type: "get",
//               data:"blogid="+blogid,
//               dataType: 'json',
//               success:function(data){
//                   console.log(data);
//                    $.each(data, function (index, item) {
//                        
//                        
//                         var oImg = item.Cid;
//
//                var img = document.createElement("img");
//
//                oImg = document.createElement("img");
//                var imsc = 'data:image/jpg;base64, ' + item.imageuser;
//                oImg.setAttribute('src', imsc);
//                oImg.setAttribute('height', 50);
//                oImg.setAttribute('width', 50);
////document.body.appendChild(oImg);
//                Cdata = $("<div id='Cphoto" + item.Cid + "' class='Cphotos'></div><div id='Cname" + item.Cid +"' class='Cname'></div><div id='Cblogs" + item.Cid + "' class='Cblogs'></div><br><br><input type='button' id="+item.Cid+" value='Share your views' class='Ccomment'><br><br>");
//
//                $('#commentarea').append(photodata);
//                $("#Cphoto" + item.Cid).append(oImg);
//                $("#Cblogs" + item.Cid).append(item.text);
//                $("#Cname" + item.Cid).append(item.user+" Said");
//
//
//
//                oImg = "";
//                    });
//                   
//               }, 
//               error: function (jqxhr, status, errorMsg) {
//            alert('Failed! ' + errorMsg);
//        }
//                
//            }); 
            
            
                   
        //e2.style.visibility = (e2.style.visibility === "visible") ? "hidden" : "visible";

   // });
      $(document).click(function (event) {
        var $target = $(event.target);
        if ($target.attr('class') === 'comment') {
            commentblogid=$target.attr('id');
            //alert(blogid);
            if ($('#commentarea').is(':hidden') ) {
               // $(Cdata).remove();
                $('#commentarea').show();
                $('#commentarea').css( 'top', event.pageY );
                $('#commentarea').css( 'left', event.pageX );
               // $('#inner').show();
              getComments(commentblogid);

            }
            else {
                $('#commentarea').hide();
                $(".Cphotos").remove();
                       $(".Cblogs").remove();
                        $(".Cname").remove();


                                //$('#inner').hide();

            }
        }
        else if ($target.closest('#commentarea').length > 0) {

        }
        else {
                 $('#commentarea').hide();
                $(".Cphotos").remove();
                       $(".Cblogs").remove();
                        $(".Cname").remove();
                        $("#statcomname").remove();
                        $("#statecom").remove();
 
        }





    });
    
    $("#commenttext").keyup(function(e){
       
        if(e.keyCode===13)
        {
            userfeed=$("#commenttext").val();
           insertComments(commentblogid,userfeed);
           
        }
        
        
        
    });
    $(document).scroll(function(e){

    // grab the scroll amount and the window height
    var scrollAmount = $(window).scrollTop();
    var documentHeight = $(document).height();
   // console.log(scrollAmount);
   // console.log(documentHeight);
    // calculate the percentage the user has scrolled down the page
    var scrollPercent = (scrollAmount / documentHeight) * 100;

    if(scrollPercent>70) {
        //var go=upperlimit+10;
        //alert("sdffsdvs");
        // run a function called doSomething
       doSomething();
                  


    }

    function doSomething() { 
        if(previoustask && dataavailable)
        {
           previoustask=false;
           
            lowerlimit=upperlimit+1;
           
           upperlimit=upperlimit+10;
           if(lowerlimit>40)
           {
               return;
           }
           bringPost(upperlimit,lowerlimit);
        }
        
   

        // do something when a user gets 50% of the way down my page

    }

});

function getComments(blogid)
{
    $("#loading").css("display", "block");
           $.ajax({
               url:"rs/blog/comments",
               Type: "get",
               data:"blogid="+blogid,
               dataType: 'json',
               success:function(data){
                   $("#loading").css("display", "none");
                   console.log(data);
                    $.each(data, function (index, item) {
                        
                        
                         var oImg = item.Cid;

                var img = document.createElement("img");

                oImg = document.createElement("img");
                var imsc = 'data:image/jpg;base64, ' + item.imageuser;
                oImg.setAttribute('src', imsc);
                oImg.setAttribute('height', 50);
                oImg.setAttribute('width', 50);
//document.body.appendChild(oImg);
                Cdata = $("<div id='Cname" + item.Cid +"' class='Cname'></div><div id='Cblogs" + item.Cid + "' class='Cblogs'></div>");

                $('#commentarea').append(Cdata);
               // $("#Cphoto" + item.Cid).append(oImg);
                $("#Cblogs" + item.Cid).append(item.text);
                $("#Cname" + item.Cid).append(item.user+" Said");



                oImg = "";
                    });
                   
               }, 
               error: function (jqxhr, status, errorMsg) {
            //alert('Failed! ' + errorMsg);
        }
                
            });
}
    
    
    
    function insertComments(blog,text)
    {
       
        var data={text: text, blogid: blog};
      $.ajax({
         url:'rs/blog/insertComment',
         type: "post",
         
         data: JSON.stringify(data),
         contentType: 'application/json; charset=utf-8',
         success:function(data){
             //$(".Cphotos").remove();
                     //  $(".Cblogs").remove();
                       // $(".Cname").remove();
                        //alert("in insert comment");
                        alert(data);
                        var newcom=$("<div id='statcomname'>"+username+"</div><div id='statecom'>"+text+"</div>");
          
                         $('#commentarea').append(newcom);

         $("commenttext").val("");
         
         
         },
          error: function (jqxhr, status, errorMsg) {
            alert('Failed! ' + errorMsg);
        }

      });
    }
    
});

           