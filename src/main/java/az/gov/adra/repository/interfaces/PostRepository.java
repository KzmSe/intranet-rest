package az.gov.adra.repository.interfaces;

import az.gov.adra.dataTransferObjects.PostDTO;
import az.gov.adra.entity.Post;
import az.gov.adra.entity.PostLd;
import az.gov.adra.entity.PostReview;
import az.gov.adra.exception.PostCredentialsException;

import java.util.List;
import java.util.Map;

public interface PostRepository {

    List<Post> findTopPostsByLastAddedTime();

    List<Post> findAllPosts(int offset);

    PostDTO findPostByPostId(int id);

    List<PostReview> findReviewsByPostId(int id);

    void addPostReview(PostReview postReview) throws PostCredentialsException;

    void addPost(Post post) throws PostCredentialsException;

    void incrementViewCountOfPostById(int id) throws PostCredentialsException;

    Map<Integer, Integer> findRespondOfPost(int employeeId, int postId);

    void updatePostRespond(PostLd postLd) throws PostCredentialsException;

    List<Post> findPostsByEmployeeId(int id, int fetchNext);

    void updatePost(Post post) throws PostCredentialsException;

    List<Post> findPostsRandomly();

    int findCountOfAllPosts();

    void deletePost(Post post) throws PostCredentialsException;

    List<Post> findPostsByKeyword(String keyword);

    void isPostExistWithGivenId(int id) throws PostCredentialsException;

}
