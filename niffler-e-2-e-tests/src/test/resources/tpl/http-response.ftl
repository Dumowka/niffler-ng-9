<html>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpResponseAttachment" -->
<head>
    <meta http-equiv="content-type" content="text/html; charset = UTF-8">
    <link type="text/css" href="https://yandex.st/highlightjs/8.0/styles/github.min.css" rel="stylesheet"/>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/highlight.min.js"></script>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/languages/xml.min.js"></script>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/languages/json.min.js"></script>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/languages/html.min.js"></script>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/languages/bash.min.js"></script>
    <script type="text/javascript">hljs.initHighlightingOnLoad();</script>

    <style>
        pre {
            white-space: pre-wrap;
        }
    </style>
</head>
<body>
<h3>Response</h3>
<div>
    <pre>
        <code>
            Response code: <#if data.responseCode??>${data.responseCode}<#else>Код ответа не найден</#if>
            URL: <#if data.url??>${data.url}<#else>URL не найден</#if>
            Body: <br><#if data.body??>${data.body}<#else>Тело запроса не найдено</#if>
        </code>
    </pre>
</div>
<#if (data.headers)?has_content>
    <div>
        <pre>
            <code>
                <h4>Headers</h4>
                <#list data.headers as name, value>
                    <b>${name}</b>: ${value}
                </#list>
            </code>
        </pre>
    </div>
</#if>
<#if (data.cookies)?has_content>
    <div>
        <div>
            <pre>
                <code>
                    <h4>Cookies</h4>
                    <#list data.cookies as name, value>
                        <b>${name}</b>: ${value}
                    </#list>
                </code>
            </pre>
        </div>
    </div>
</#if>
</body>
</html>