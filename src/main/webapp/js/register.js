///* 
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//
///* 
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
$(document).ready(function () {




    $("#reg").click(function (e) {
        e.preventDefault();

        var imgvalid = false;
        var formURL = "imageupload";
        var data = $("#myform").serialize();
        var jForm = new FormData();
        jForm.append("name", $('#name').val());
        jForm.append("pass", $('#pass').val());
        // jForm.append("image", $('#image').val());

        jForm.append("image", $('#image').get(0).files[0]);
        var imgname = $("#image").val();
        var name = $("#name").val();
        var pass = $("#pass").val();
        var _validFileExtensions = [".jpg", ".jpeg", ".bmp", ".gif", ".png"];
        if (imgname.length > 0)
        {
            for (var j = 0; j < _validFileExtensions.length; j++) {
                var sCurExtension = _validFileExtensions[j];
                if (imgname.substr(imgname.length - sCurExtension.length, sCurExtension.length).toLowerCase() === sCurExtension.toLowerCase()) {

                    imgvalid = true;



                }

            }
        }



        if (imgvalid !== true)
        {
            alert('plaese emter the right image');
        }
        else if (pass === "") {
            alert('password field cannot be empty');
        }
        else if (name === "")
        {
            alert('username field cannot be empty');
        }
        else {
            $.ajax({
                   url: formURL,
                   type: "POST",
                   data: jForm,
                   processData: false,
                   contentType: false,
                   cache: false,
                   //processData: false,
                   success: function (data, textStatus, jqXHR) {
                       alert(data);
                       if(data==="ARY")
                       {
                           alert('This username already Exist Plaes Pick Another one!');
                       }
                       if(data==="done")
                       {
                           alert('Your Image has been Uploaded');
                       }
                       },
                         error: function(jqxhr, status, errorMsg) {
                  alert('Failed! ' + errorMsg);
                    }
                 
               });
        }



    });



});