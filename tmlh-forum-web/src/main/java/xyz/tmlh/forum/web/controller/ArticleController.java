package xyz.tmlh.forum.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xyz.tmlh.core.enums.PublishType;
import xyz.tmlh.core.model.ArticleModel;
import xyz.tmlh.core.model.CommentModel;
import xyz.tmlh.core.model.UserModel;
import xyz.tmlh.core.model.data.CommentDo;
import xyz.tmlh.core.service.ArticleService;
import xyz.tmlh.core.service.CommentService;
import xyz.tmlh.core.service.SocialService;
import xyz.tmlh.core.service.UserService;
import xyz.tmlh.core.suport.SqlPrefix;
import xyz.tmlh.forum.annotation.SysLog;
import xyz.tmlh.forum.util.user.CurrentUserUtils;
import xyz.tmlh.forum.web.controller.vo.CommentVo;
import xyz.tmlh.forum.web.vo.ArticleVo;
import xyz.tmlh.security.browser.suport.ResultBean;

/**
 * <p>
 *    文章
 * </p>
 *
 * @author TianXin
 * @since 2019年4月2日下午7:50:47
 */
@Api("文章管理控制器")
@RequestMapping("/article")
@Controller
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private SocialService socialService;
    
    @SysLog("文章列表分页-异步")
    @ApiOperation("文章列表分页")
    @ResponseBody
    @GetMapping({"/",""})
    public ResultBean list(@RequestParam(defaultValue="1")int currPage ,@RequestParam(defaultValue="10") int pageSize,
        @RequestParam(required=false) Integer id , @RequestParam(required=false) PublishType publishType) {
        ResultBean resultBean = new ResultBean();
        IPage<ArticleModel> page = new Page<>(currPage, pageSize);
        LambdaQueryWrapper<ArticleModel> wapper = new LambdaQueryWrapper<ArticleModel>();
        if(id != null && id > 0) {
            wapper.eq(ArticleModel::getUser, id);
        }
        if(publishType != null) {
            wapper.eq(ArticleModel::getPublishType, publishType);
        }
        wapper.orderByDesc(ArticleModel::getUpdateTime);
        
        IPage<ArticleModel> articlePage = articleService.selectUserPage(page ,wapper);
        articlePage.getRecords().stream().forEach(e -> {
            int count = commentService.count(new LambdaQueryWrapper<CommentModel>().eq(CommentModel::getArticleId, e.getId()));
            e.setCommentCount((long)count);
        });
        
        resultBean.putResult("articlePage", articlePage);
        return resultBean;
    }
    
    @SysLog("查看文章")
    @ApiOperation("查看文章")
    @GetMapping("/{id}")
    public String article(@PathVariable("id")Integer id , Model model) {
        ArticleModel article = articleService.getById(id);
        UserModel user = userService.getById(article.getUser().getId());
        //更新点击数
        articleService.updateById(new ArticleModel(id, article.getClickNum() + 1 ));
        
        model.addAttribute("article", new ArticleVo(article, user));
        
        QueryWrapper<CommentModel> qWrapper = new QueryWrapper<>();
        qWrapper.eq("com.article_id" , id);
        qWrapper.isNull("com.comment_id");
        List<CommentVo> CommentVos = commentService.findAll(qWrapper).stream().map(e ->{
            List<CommentDo> commentChildList = commentService.findAll(new QueryWrapper<CommentModel>().eq("com.comment_id", e.getId()));
            return new CommentVo(e, commentChildList);
        }).collect(Collectors.toList());
        model.addAttribute("commentList",CommentVos );
        
        LambdaQueryWrapper<ArticleModel> wapper = new LambdaQueryWrapper<ArticleModel>()
            .eq(ArticleModel::getUser, user.getId())
            .select(ArticleModel::getId , ArticleModel::getTitle)
            .orderByDesc(ArticleModel::getUpdateTime)
            .last(SqlPrefix.LIMIT + 10);
        model.addAttribute("articleList", articleService.list(wapper));
        boolean isFollow = false;
        if(CurrentUserUtils.isExistUser()) {
            isFollow = socialService.selectFollow(CurrentUserUtils.getUser().getId() , user.getId());
        }
        model.addAttribute("isFollow", isFollow);
        
        //文章和问答数量粉丝数量
        model.addAttribute("articleCount" , articleService.selectByUser(id, PublishType.ARTICLE));
        model.addAttribute("questionCount" , articleService.selectByUser(id, PublishType.QUESTION));
        
        return "article";
    }
    
}
