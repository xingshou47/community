//提交回复
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    if (!content.trim()){
        alert("回复内容不能为空")
        return;
    }
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
                window.location.reload();
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

//展开二级回复列表
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    // console.log(id);
    var comments = $("#comment-"+id);
    //自动判断comments中是否存在in如果有则在点击时去除反之则添加
    comments.toggleClass("in")
    //进行判断class中是否有in
    if (comments.hasClass("in")){
        // console.log("进入if方法之中，表示该类中有in")
        e.classList.add("active")
    }else {
        // console.log("没有进入if方法之中，表示该类中没有in")
        e.classList.remove("active")
    }
}