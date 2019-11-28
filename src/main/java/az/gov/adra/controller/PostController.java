package az.gov.adra.controller;

import az.gov.adra.constant.MessageConstants;
import az.gov.adra.constant.PostConstants;
import az.gov.adra.dataTransferObjects.PostDTO;
import az.gov.adra.entity.*;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.UserCredentialsException;
import az.gov.adra.exception.PostCredentialsException;
import az.gov.adra.service.interfaces.UserService;
import az.gov.adra.service.interfaces.PostService;
import az.gov.adra.util.ResourceUtil;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Value("${file.upload.path.win}")
    private String imageUploadPath;
    private final int maxFileSize = 3145728;
    private final String defaultPostHexCode = "706F7374735C64656661756C745F706F73742E6A7067";


    @GetMapping("/posts")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findPosts(@RequestParam(name = "page", required = false) Integer page,
                                     HttpServletResponse response) throws PostCredentialsException {
        if (ValidationUtil.isNull(page)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = postService.findCountOfAllPosts();
        int totalPages = 0;
        int offset = 0;

        if (total != 0) {
            totalPages = (int) Math.ceil((double) total / 10);

            if (page != null && page >= totalPages) {
                offset = (totalPages - 1) * 10;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 10;
            };
        }

        List<Post> posts = postService.findAllPosts(offset);
        for (Post post : posts) {
            if (post.getImgUrl() == null) {
                continue;
            }
            post.setImgUrl(ResourceUtil.convertToString(post.getImgUrl()));
        }

        response.setIntHeader("Total-Pages", totalPages);
        return GenericResponse.withSuccess(HttpStatus.OK, "list of posts", posts);
    }

    @GetMapping("/posts/{postId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findPostById(@PathVariable(name = "postId", required = false) Integer id) throws PostCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        postService.isPostExistWithGivenId(id);

        PostDTO postDTO = postService.findPostByPostId(id);
        postDTO.setImgUrl(ResourceUtil.convertToString(postDTO.getImgUrl()));

        return GenericResponse.withSuccess(HttpStatus.OK, "specific post by id", postDTO);
    }

    @GetMapping("/posts/{postId}/reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findReviewsByPostId(@PathVariable(name = "postId", required = false) Integer id,
                                               @RequestParam(name = "offset", required = false) Integer offset) throws PostCredentialsException {
        if (ValidationUtil.isNull(id) || ValidationUtil.isNull(offset)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        postService.isPostExistWithGivenId(id);

        List<PostReview> reviews = postService.findReviewsByPostId(id, offset);
        return GenericResponse.withSuccess(HttpStatus.OK, "reviews of specific post", reviews);
    }

    @PostMapping("/posts/{postId}/reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addPostReview(@PathVariable(value = "postId") Integer id,
                                  @RequestParam(value = "description") String description) throws PostCredentialsException {
        if (ValidationUtil.isNull(id) || ValidationUtil.isNullOrEmpty(description)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        postService.isPostExistWithGivenId(id);

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        PostReview review = new PostReview();
        Post post = new Post();
        post.setId(id);
        review.setUser(user);
        review.setPost(post);
        review.setDescription(description);
        review.setDateOfReg(LocalDateTime.now().toString());
        review.setStatus(PostConstants.POST_REVIEW_STATUS_ACTIVE);

        //  URI location = ServletUriComponentsBuilder
        //      .fromCurrentRequest()
        //      .path("/{activityId}")
        //      .buildAndExpand(savedReview.getId()).toUri();
        //
        //  return ResponseEntity.created(location).build();        return type --->ResponseEntity<Object>

        postService.addPostReview(review);
    }

    @PostMapping("/posts")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addPost(@RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "description", required = false) String description,
                            @RequestParam(value = "file", required = false) MultipartFile multipartFile) throws PostCredentialsException, IOException {

        if (ValidationUtil.isNullOrEmpty(title, description)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (!multipartFile.isEmpty()) {
            if (!(multipartFile.getOriginalFilename().endsWith(".jpg")
                    || multipartFile.getOriginalFilename().endsWith(".jpeg")
                    || multipartFile.getOriginalFilename().endsWith(".png"))) {
                throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INVALID_FILE_TYPE);
            }

            if (multipartFile.getSize() >= maxFileSize) {
                throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_FILE_SIZE_MUST_BE_SMALLER_THAN_5MB);
            }
        }

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        Post post = new Post();
        post.setUser(user);
        post.setTitle(title);
        post.setDescription(description);
        post.setViewCount(0);
        post.setDateOfReg(LocalDateTime.now().toString());
        post.setStatus(PostConstants.POST_STATUS_ACTIVE);

        if (!multipartFile.isEmpty()) {
            Path pathToSaveFile = Paths.get(imageUploadPath, "posts", user.getUsername());

            if (!Files.exists(pathToSaveFile)) {
                Files.createDirectories(pathToSaveFile);
            }

            String fileName = UUID.randomUUID() + "##" + multipartFile.getOriginalFilename();
            Path fullFilePath = Paths.get(pathToSaveFile.toString(), fileName);
            Files.copy(multipartFile.getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);
            Path pathToSaveDb = Paths.get("posts", user.getUsername(), fileName);

            post.setImgUrl(DatatypeConverter.printHexBinary(pathToSaveDb.toString().getBytes()));

        } else {
            post.setImgUrl(defaultPostHexCode);
        }

        postService.addPost(post);
    }

    @PutMapping("/posts/{postId}/responds")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    public void updatePostRespond(@PathVariable(value = "postId", required = false) Integer id,
                                  @RequestParam(value = "respond", required = false) Integer respond) throws PostCredentialsException {
        if (ValidationUtil.isNull(id) || ValidationUtil.isNull(respond)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (respond.compareTo(PostConstants.POSTLD_STATUS_ACTIVE) != 0 && respond.compareTo(PostConstants.POSTLD_STATUS_INACTIVE) != 0) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ACTIVITY_RESPOND_MUST_BE_1_OR_0);
        }

        postService.isPostExistWithGivenId(id);

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        PostLd postLd = new PostLd();
        Post post = new Post();
        post.setId(id);
        postLd.setPost(post);
        postLd.setUser(user);
        postLd.setLikeDislike(respond);
        postLd.setDateOfReg(LocalDateTime.now().toString());
        postLd.setStatus(PostConstants.POSTLD_STATUS_ACTIVE);

        postService.updatePostRespond(postLd);
    }

    @GetMapping("/users/{username}/posts")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findPostsByUsername(@PathVariable(value = "username",required = false) String username,
                                               @RequestParam(name = "page", required = false) Integer page,
                                               HttpServletResponse response) throws PostCredentialsException, UserCredentialsException {
        if (ValidationUtil.isNull(username) || ValidationUtil.isNull(page)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = postService.findCountOfAllPostsByUsername(username);
        int totalPages = 0;
        int offset = 0;

        if(total != 0) {
            totalPages = (int) Math.ceil((double) total / 3);

            if (page != null && page >= totalPages) {
                offset = (totalPages - 1) * 3;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 3;
            };
        }

        userService.isUserExistWithGivenUsername(username);

        List<Post> posts = postService.findPostsByUsername(username, offset);
        for (Post post : posts) {
            if (post.getImgUrl() == null) {
                continue;
            }
            post.setImgUrl(ResourceUtil.convertToString(post.getImgUrl()));
        }

        response.setIntHeader("Total-Pages", totalPages);
        return GenericResponse.withSuccess(HttpStatus.OK, "posts of specific employee", posts);
    }

    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    public void updatePost(@PathVariable(value = "postId", required = false) Integer id,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "description", required = false) String description,
                                          @RequestParam(value = "file", required = false) MultipartFile multipartFile) throws PostCredentialsException, IOException {
        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        Post post = new Post();

        if (ValidationUtil.isNull(id) || ValidationUtil.isNullOrEmpty(title, description)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (ValidationUtil.isNull(multipartFile) || multipartFile.isEmpty()) {
            post.setImgUrl("none");

        } else {
            if (!(multipartFile.getOriginalFilename().endsWith(".jpg")
                    || multipartFile.getOriginalFilename().endsWith(".jpeg")
                    || multipartFile.getOriginalFilename().endsWith(".png"))) {
                throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INVALID_FILE_TYPE);
            }

            if (multipartFile.getSize() >= maxFileSize) {
                throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_FILE_SIZE_MUST_BE_SMALLER_THAN_5MB);
            }

            Path pathToSaveFile = Paths.get(imageUploadPath, "posts", user.getUsername());

            if (!Files.exists(pathToSaveFile)) {
                Files.createDirectories(pathToSaveFile);
            }

            String fileName = UUID.randomUUID() + "##" + multipartFile.getOriginalFilename();
            Path fullFilePath = Paths.get(pathToSaveFile.toString(), fileName);
            Files.copy(multipartFile.getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);
            Path pathToSaveDb = Paths.get("posts", user.getUsername(), fileName);
            post.setImgUrl(DatatypeConverter.printHexBinary(pathToSaveDb.toString().getBytes()));
        }

        post.setId(id);
        post.setUser(user);
        post.setTitle(title);
        post.setDescription(description);

        postService.updatePost(post);
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    public void deletePost(@PathVariable(value = "postId", required = false) Integer id) throws PostCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        postService.isPostExistWithGivenId(id);

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        Post post = new Post();
        post.setId(id);
        post.setUser(user);

        postService.deletePost(post);
    }

    @GetMapping("/posts/keyword")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findPostsByKeyword(@RequestParam(value = "page", required = false) Integer page,
                                              @RequestParam(value = "keyword", required = false) String keyword,
                                              HttpServletResponse response) throws PostCredentialsException {
        if (ValidationUtil.isNull(page) || ValidationUtil.isNullOrEmpty(keyword)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = postService.findCountOfAllPostsByKeyword(keyword.trim());
        int totalPages = 0;
        int offset = 0;

        if (total != 0) {
            totalPages = (int) Math.ceil((double) total / 10);

            if (page != null && page >= totalPages) {
                offset = (totalPages - 1) * 10;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 10;
            };
        }

        List<Post> posts = postService.findPostsByKeyword(keyword.trim(), offset);
        for (Post post : posts) {
            if (post.getImgUrl() == null) {
                continue;
            }
            post.setImgUrl(ResourceUtil.convertToString(post.getImgUrl()));
        }

        response.setIntHeader("Total-Pages", totalPages);
        return GenericResponse.withSuccess(HttpStatus.OK, "posts by keyword", posts);
    }

//    @GetMapping("/posts/random")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public GenericResponse findPostsRandomly() {
//        List<Post> posts = postService.findPostsRandomly();
//        return GenericResponse.withSuccess(HttpStatus.OK, "random posts", posts);
//    }

    @GetMapping("/posts/top-three")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findPostsByLastAddedTime() {
        List<Post> posts = postService.findTopPostsByLastAddedTime();
        for (Post post : posts) {
            if (post.getImgUrl() == null) {
                continue;
            }
            post.setImgUrl(ResourceUtil.convertToString(post.getImgUrl()));
        }
        return GenericResponse.withSuccess(HttpStatus.OK, "last added posts", posts);
    }

//    @GetMapping("/posts/count")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public GenericResponse findCountOfAllPosts() {
//        int count = postService.findCountOfAllPosts();
//        return GenericResponse.withSuccess(HttpStatus.OK, "count of all posts", count);
//    }

    @PutMapping("/posts/{postId}/view-count")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    public void incrementViewCountOfPostById(@PathVariable(name = "postId", required = false) Integer id) throws PostCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        postService.isPostExistWithGivenId(id);

        postService.incrementViewCountOfPostById(id);
    }

    @GetMapping("/posts/{postId}/respond")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findRespondOfPost(@PathVariable(name = "postId", required = false) Integer id) throws PostCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        postService.isPostExistWithGivenId(id);

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        Map<Integer, Integer> respond = postService.findRespondOfPost(user.getUsername(), id);
        return GenericResponse.withSuccess(HttpStatus.OK, "respond of specific post", respond);
    }

}
