<#import "./layout/inc.ftl" as inc>
<#import "./layout/nav.ftl" as nav>
<#import "./layout/message-js.ftl" as message>

<!DOCTYPE html>
<html lang="en">
<@inc.head '首页'>
</@inc.head>
<body>
<@nav.topbar>
</@nav.topbar>
<div class="container">

		<div class="row clearfix">
			<div class="col-md-12 column">


				<div id="carousel-example-generic" class="carousel slide"
					data-ride="carousel">
					<!-- Indicators -->
					<ol class="carousel-indicators">
						<li data-target="#carousel-example-generic" data-slide-to="0"
							class="active"></li>
						<li data-target="#carousel-example-generic" data-slide-to="1"></li>
						<li data-target="#carousel-example-generic" data-slide-to="2"></li>
					</ol>

					<!-- Wrapper for slides -->
					<div class="carousel-inner" role="listbox">
						<div class="item active">
							<img src="https://tmlh98.gitee.io/image-repository/img/spring_monkey/lunbo-1.jpg" class="img-size" alt="..."/>
							<div class="carousel-caption">...</div>
						</div>
						<div class="item">
							<img src="https://tmlh98.gitee.io/image-repository/img/spring_monkey/lunbo-2.jpg" class="img-size" alt="..."/>
							<div class="carousel-caption">...</div>
						</div>
						<div class="item">
							<img src="https://tmlh98.gitee.io/image-repository/img/spring_monkey/lunbo-3.jpg" class="img-size" alt="..."/>
							<div class="carousel-caption">...</div>
						</div>
					</div>

					<!-- Controls -->
					<a class="left carousel-control" href="#carousel-example-generic"
						role="button" data-slide="prev"> <span
						class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
						<span class="sr-only">Previous</span>
					</a> <a class="right carousel-control" href="#carousel-example-generic"
						role="button" data-slide="next"> <span
						class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
						<span class="sr-only">Next</span>
					</a>
				</div>



			</div>
		</div>


		<div class="row clearfix" style="margin-top: 25px">
			<div class="col-md-9 column">

				<div class="list-group"  id="article-list" >
					<div class="list-group-item">
						<span class="glyphicon glyphicon-th-list" aria-hidden="true" style="font-size: 17px">&nbsp;社区动态</span>
					</div>
					
				</div>



				<button id="add-article" type="button" class="btn btn-default btn-primary btn-block">点击载入更多</button>
			</div>

			<div class="col-md-3 column">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h2 class="panel-title">推荐作者</h2>
					</div>
					<div class="panel-footer">
						<div class="card-body text-center">
							<#list userList as user>
							<a href="/user/${user.id}" > 
								<img src="${user.imageUrl}"
								class="img-rounded " width="45px" height="45px" />
							</a> 
							</#list>
						</div>

					</div>
				</div>

				<div class="panel panel-default">
					<div class="panel-heading">
						<h2 class="panel-title">热门标签</h2>
					</div>
					<div class="panel-body">
						<a href="#"
							class="text-white badge badge-secondary font-weight-normal">Spring Boot</a>
						<a href="#"
							class="text-white badge badge-secondary font-weight-normal">MangoDB</a>
						<a href="#" class="text-white badge badge-secondary font-weight-normal">Java</a>
						<a href="#" class="text-white badge badge-secondary font-weight-normal">Dubbo</a>
						<a href="#" class="text-white badge badge-secondary font-weight-normal">Ribbon</a>
						<a href="#" class="text-white badge badge-secondary font-weight-normal">Mysql</a>
						<a href="#" class="text-white badge badge-secondary font-weight-normal">Linux</a>
						<a href="#" class="text-white badge badge-secondary font-weight-normal">Solr</a>
						<a href="#" class="text-white badge badge-secondary font-weight-normal">Redis</a>
						<a href="#" class="text-white badge badge-secondary font-weight-normal">JVM</a>
					</div>
				</div>

			</div>
		</div>

	</div>
<div>
<#include "./layout/footer.ftl">
</body>
<@inc.script>

<script src="/asserts/js/apple-data.js"></script>
<script type="text/javascript">
	var current = 1; 
	$(function(){
		getArticlePage(current);
		$('#add-article').click(function(){
			getArticlePage(current);
		});
	});
	
	function getArticlePage(currPage){
		$.ajax({
  		  method: "GET",
  		  url: '/article',
  		  data: { 
  			  currPage: currPage,
  			  pageSize: 10,
  			  },
  		  success: function(data){
  			  if(data.code == '0'){
  				var articlePage = data.result.articlePage;
  				if(articlePage.records == 0){
					infoNotify('没有更多了!');
				}
  				$.each(articlePage.records, function(index, article) {
  					showArts(article,$('#article-list'));
  					current = articlePage.current + 1;
  			    });
  				  
  			  }else{
  				dangerNotify(data.message);
  			  }
  		  }
  		});
	}
	

</script>
</@inc.script>
<@message.messageNotify></@message.messageNotify>

</html>
