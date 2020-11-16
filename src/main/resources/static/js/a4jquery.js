$(document).ready( function () {
    $("#subProtocol option").change(function () {
        let map = new Map([
            ['HTTP', 'http://www.example.com'],
            ['HTTPS', 'https://www.example.com'],
            ['Email', 'test@example.com'],
            ['SMS', '+12223334444'],
            ['Amazon SQS', 'arn:aws:sqs:us-east-1:123456789012:MyQueue'],
            ['AWS Lambda', 'arn:aws:lambda:us-east-1:123456789012:function:MyLambdaFunction']
        ]);
        let target = $("#subEndpoint option:selected").val();
        $("#subEndpoint").attr("placeholder", map.get(target));
    });
});

