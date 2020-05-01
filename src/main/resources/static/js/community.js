function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    //在网页中能够打印出来
    // console.log(questionId)
    // console.log(content)
    $.ajax({
        type:"POST",
        url:"/comment",
        data:JSON.stringify({
            "parentId":questionId,
            "content":content,
            "type":1
        }),
        success:function (response) {
            if (response.code == 200){
                $("#comment_section").hide()
            }else {
                if (response.code == 2003){
                    var isAccepted = confirm(response.message);
                    if (isAccepted){
                        window.open("https://github.com/login/oauth/authorize?client_id=Iv1.f63b1904b3224567&redirect_uri=http://localhost:8888/callback&scope=user&state=1")
                        window.localStorage.setItem("closable",true);
                    }
                }
                alert(response.message)
            }
            console.log(response);
        },
        contentType:"application/json",
        dataType:"json"
    });
}