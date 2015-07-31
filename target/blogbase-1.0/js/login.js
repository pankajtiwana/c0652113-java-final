/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {




    $("#log").click(function (e) {
        e.preventDefault();

        var username = document.getElementById("name").value;
        var password = document.getElementById("pass").value;
        var data = {"username": username, "password": password};
//alert('working');
        $.ajax({
            url: "userlog",
            method: "GET",
            data: data,
            success: function (data) {
                if (data === "false")
                {
                    alert("username is wrong");
                }
                if (data === "true")
                {
                    window.location.replace("blogs.html");
                }

            }


        });




    });



});
