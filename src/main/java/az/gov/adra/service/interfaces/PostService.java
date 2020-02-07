package az.gov.adra.service.interfaces;

import az.gov.adra.dataTransferObjects.PostDTO;
import az.gov.adra.dataTransferObjects.RespondDTO;
import az.gov.adra.entity.Post;
import az.gov.adra.entity.PostLd;
import az.gov.adra.entity.PostReview;
import az.gov.adra.exception.PostCredentialsException;

import java.util.List;
import java.util.Map;

public interface PostService {

    List<Post> findTopPostsByLastAddedTime();

    List<Post> findAllPosts(int offset);

    PostDTO findPostByPostId(int id);

    List<PostReview> findReviewsByPostId(int id, int fetchNext);

    void addPostReview(PostReview postReview) throws PostCredentialsException;

    void addPost(Post post) throws PostCredentialsException;

    void incrementViewCountOfPostById(int id) throws PostCredentialsException;

    RespondDTO findRespondOfPost(String username, int postId);

    void updatePostRespond(PostLd postLd) throws PostCredentialsException;

    List<Post> findPostsByUsername(String username, int offset);

    void updatePost(Post post) throws PostCredentialsException;

    List<Post> findPostsRandomly();

    int findCountOfAllPosts();

    int findCountOfAllPostsByKeyword(String keyword);

    int findCountOfAllPostsByUsername(String username);

    void deletePost(Post post) throws PostCredentialsException;

    List<Post> findPostsByKeyword(String keyword, int offset);

    void isPostExistWithGivenId(int id) throws PostCredentialsException;

}
