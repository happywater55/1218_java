i = 0
tran_from_type = $('#translate_input_type').val();
tran_to_type = $('#translate_output_type').val();

tran_input = $('#translate_input').val();
$(document).ready(
    function () {

        setInterval('translate()', 5000);

    }
)




function addCookie(tran_input,tran_from_type,tran_to_type,dst) {
    var word_cookie = $.cookie('word');
    var word_cookie_list;
    if (word_cookie==null){
        word_cookie_list = [];
    }
    else {
        word_cookie_list = JSON.parse(word_cookie);
    }

    var obj =
        {
            "tran_input":tran_input,
            "tran_from_type":tran_from_type,
            "tran_to_type":tran_to_type,
            "dst":dst
        };
    console.log(word_cookie_list)
    word_cookie_list.push(obj);

    var new_cookie = JSON.stringify(word_cookie_list);
    $.cookie('word',new_cookie);

}

function get_word_from_cookie(tran_input,tran_from_type,tran_to_type){
    var word_cookie = $.cookie('word');
    if (word_cookie==null){
        return null;
    }
    var word_cookie_list = JSON.parse(word_cookie);
    for(var i =0;i<word_cookie_list.length;i++){
        var temp = word_cookie_list[i];
        if (temp["tran_input"]==tran_input && temp["tran_from_type"]==tran_from_type && temp["tran_to_type"]==tran_to_type) {
            return temp["dst"];
        }
    }
    return null;
}

function translate() {
    var tran_from_type_now = $('#translate_input_type').val();
    var tran_to_type_now = $('#translate_output_type').val();

    var tran_input_now = $('#translate_input').val();

    var flag = 0;
    if (tran_from_type_now != tran_from_type_now || tran_to_type != tran_to_type_now || tran_input != tran_input_now) {
        //内容变化才发送请求


        tran_from_type = $('#translate_input_type').val();
        tran_to_type = $('#translate_output_type').val();

        tran_input = $('#translate_input').val();
        console.log(tran_from_type);
        console.log(tran_to_type);
        console.log(tran_input);
        console.log(i++);

        var _output = get_word_from_cookie(tran_input,tran_from_type,tran_to_type);
        if (_output==null){
            flag = 1;
        }
        else{
            $("#translate_output").val(_output);
        }

        if (flag == 1) {
            $.ajax({
                type: 'post',
                url: '/FanyiServlet',
                data: {from: tran_from_type, to: tran_to_type, query: tran_input},
                dataType: 'json',
                success: function (response) {
                    var code = response.code;
                    if (code == '1') {
                        var dst = response.dst
                        console.log(dst);
                        $("#translate_output").val(dst);
                        addCookie(tran_input,tran_from_type,tran_to_type,dst);
                    } else {
                        alert('出错了！！！')
                    }
                }
            })
        }
    }


}