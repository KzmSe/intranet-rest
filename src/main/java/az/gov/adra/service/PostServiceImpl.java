package az.gov.adra.service;

import az.gov.adra.dataTransferObjects.PostDTO;
import az.gov.adra.entity.Post;
import az.gov.adra.entity.PostLd;
import az.gov.adra.entity.PostReview;
import az.gov.adra.exception.PostCredentialsException;
import az.gov.adra.repository.interfaces.PostRepository;
import az.gov.adra.service.interfaces.PostService;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> findTopPostsByLastAddedTime() {
        return postRepository.findTopPostsByLastAddedTime();
    }

    @Override
    public List<Post> findAllPosts(int offset) {
        return postRepository.findAllPosts(offset);
    }

    @Override
    public PostDTO findPostByPostId(int id) {
        return postRepository.findPostByPostId(id);
    }

    @Override
    public List<PostReview> findReviewsByPostId(int id) {
        return postRepository.findReviewsByPostId(id);
    }

    @Override
    public void addPostReview(PostReview postReview) throws PostCredentialsException {
        postRepository.addPostReview(postReview);
    }

    @Override
    public void addPost(Post post) throws PostCredentialsException {
        postRepository.addPost(post);
    }

    @Override
    public void incrementViewCountOfPostById(int id) throws PostCredentialsException {
        postRepository.incrementViewCountOfPostById(id);
    }

    @Override
    public Map<Integer, Integer> findRespondOfPost(int employeeId, int postId) {
        return postRepository.findRespondOfPost(employeeId, postId);
    }

    @Override
    public void updatePostRespond(PostLd postLd) throws PostCredentialsException {
        postRepository.updatePostRespond(postLd);
    }

    @Override
    public List<Post> findPostsByEmployeeId(int id, int fetchNext) {
        return postRepository.findPostsByEmployeeId(id, fetchNext);
    }

    @Override
    public void updatePost(Post post) throws PostCredentialsException {
        postRepository.updatePost(post);
    }

    @Override
    public List<Post> findPostsRandomly() {
        return postRepository.findPostsRandomly();
    }

    @Override
    public int findCountOfAllPosts() {
        return postRepository.findCountOfAllPosts();
    }

    @Override
    public void deletePost(Post post) throws PostCredentialsException {
        postRepository.deletePost(post);
    }

    @Override
    public List<Post> findPostsByKeyword(String keyword) {
        return postRepository.findPostsByKeyword(keyword);
    }

    @Override
    public void isPostExistWithGivenId(int id) throws PostCredentialsException {
        postRepository.isPostExistWithGivenId(id);
    }

}
