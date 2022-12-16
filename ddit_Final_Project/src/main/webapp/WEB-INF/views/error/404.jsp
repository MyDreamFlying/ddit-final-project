<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

  <head>

    <!-- SITE TITTLE -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>404 Not Found</title>

    <!-- Plugins css Style -->
    <link href='${pageContext.request.contextPath }/resources/js/fontawesome-5.15.2/css/all.min.css' rel='stylesheet'>
    <link href='${pageContext.request.contextPath }/resources/js/fontawesome-5.15.2/css/fontawesome.min.css' rel='stylesheet'>

    <!-- GOOGLE FONT -->
    <link href='https://fonts.googleapis.com/css?family=Montserrat:400,500,600,700' rel='stylesheet'>

    <!-- CUSTOM CSS -->
    <link href="${pageContext.request.contextPath }/resources/css/star-color2.css" id="option_style" rel="stylesheet">


    <!-- FAVICON -->
    <link rel="shortcut icon" type="image/png" href="${pageContext.request.contextPath }/resources/images/favicon.png" />

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
      <![endif]-->

  </head>

  <body>

    <!-- ====================================
    ———	CONTACT SECTION
    ===================================== -->
    <section style="background-image: url(${pageContext.request.contextPath }/resources/images/error/not-found-bg.jpg); background-size: cover;">
      <div class="container">
        <div class="row text-center align-items-center justify-content-center vh-100">
          <div class="col-md-10 col-lg-8 col-xl-6">
            <div class="mb-7 mb-lg-9">
              <img class="img-fluid lazyestload" data-src="${pageContext.request.contextPath }/resources/images/error/404_Page.png" src="${pageContext.request.contextPath }/resources/images/error/404_Page.png" alt="image">
            </div>
            
            <p class="text-white h3 mb-7 mb-lg-9">원하시는 페이지를 찾을 수 없습니다.</p>
            <p class="text-white">찾으려는 페이지의 주소가 잘못입력되었거나<br>주소의 변경 혹은 삭제로 인해 사용하실 수 없습니다.<br>입력하신 페이지의 주소가 정확한지 다시 한번 확인해주세요.</p>

            <div class="row justify-content-center">
             	<div class="">
              		<a href="${pageContext.request.contextPath }/" class="btn btn-primary text-uppercase">Go home</a>
            	</div>
            </div>

           
          </div>
        </div>
      </div>
    </section>


    <!-- Javascript -->
    <script src="${pageContext.request.contextPath }/resources/js/jquery/jquery-3.4.1.min.js"></script>
    <script src="${pageContext.request.contextPath }/resources/js/bootstrap-5.1.3-dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath }/resources/js/lazyestload/lazyestload.js"></script>
    <script src="${pageContext.request.contextPath }/resources/js/star.js"></script>

  </body>
</html>