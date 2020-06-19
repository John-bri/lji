package au.com.letsjoinin.android.MVP.interfaces;

import au.com.letsjoinin.android.MVP.model.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    @POST("User/RegisterUser")
    Call<SignUpServerMessage> RegisterUser(@Body UserDataReq input);


    @POST("User/ValidateOTP")
    Call<LoginResponse> ValidateOTP(@Body OTPData input);


    @PUT("User/ResendOTP")
    Call<ResendOTPResponse> ResendOTP(@Body ResendOTPReq input);


    @POST("User/ResetPassword")
    Call<LoginResponse> ResetPassword(@Body ResetPasswordReq input);

    @POST("User/ChangeUserPassword")
    Call<ServerMessageData> ChangeUserPassword(@Body ChangePasswordReq input);

    @POST("Content/CreateContent")
    Call<CreateTopicResult> CreateContent(@Body CreateContentData input);



    @POST("Content/GetContentByID")
    Call<GetContentByIdRes> GetContentByID(@Body GetContentByIDReq input);




    @POST("Chat/GetContentGroups")
    Call<GetContentGroupRes> GetContentGroups(@Body GetContentGroupsReq input);



    @POST("Chat/JoinGroup")
    Call<JoinGroupsResponse> JoinGroup(@Body JoinGroupsReq input);



    @PUT("Content/UpdateContent")
    Call<ServerMessageData>UpdateContent(@Body CreateContentData input);


    @POST("Content/GetUserContents")
    Call<SearchUserData> GetUserContents(@Body ResendOTPReq input);



    @GET("Chat/ValidateChatContent")
    Call<ServerMessageData> ValidateChatContent(@Query("ChatContent") String ChatContent);


    @POST("Chat/GetContentTopOpinions")
    Call<GetContentTopOpinionsRes> GetContentTopOpinions(@Body GetContentTopOpinionsReq input);



    @POST("Chat/PostImageContent")
    Call<PostImageResponse> PostImageContent(@Body PostImageReq input);




    @DELETE("Chat/DeleteImageContent")
    Call<ServerMessageData> DeleteImageContent(@Query("MediaPath") String MediaPath);




    @POST("Notification/GetUserNotifications")
    Call<NotificationRes> GetUserNotifications(@Body ResendOTPReq input);




    @POST("Content/SearchTimeline")
    Call<SearchUserData> SearchTimeline(@Body SearchReq input);


    @POST("Channel/GetChannelByID")
    Call<ChannelDataRes> GetChannelByID(@Body ChannelDataReq input);




    @PUT("Channel/SetFollowChannel")
    Call<ServerMessageData> SetFollowChannel(@Body SetFollowReq input);




    @PUT("Channel/SetUnfollowChannel")
    Call<ServerMessageData> SetUnFollowChannel(@Body SetUnFollowReq input);



    @PUT("Content/SetFavourite")
    Call<ServerMessageData> SetFavourite(@Body SetFavReqClass input);


    @PUT("Content/RemoveFavourite")
    Call<ServerMessageData> RemoveFavourite(@Body RemoveFavReqClass input);


    @POST("User/ValidateLoginWithSM")
    Call<LoginResponse> ValidateLogin(@Body LoginData input);





    @PUT("User/UpdateUser")
    Call<ServerMessageData> UpdateUser(@Body EditUserData input);


    @GET("User/VerifyForgotPassword")
    Call<PasswordOTPResponse> VerifyForgotPassword(@Query("UserEmailId") String UserEmailId);


    @GET("Enterprise/GetEnterpriseSecurityQuestion")
    Call<GetEnterpriseSecurityQuestionData> SecurityQuestion(@Query("EnterpriseID") String EnterpriseID);

    @GET("Content/EncryptSharingLink")
    Call<String> EncryptSharingLink(@Query("StringToEncrypt") String StringToEncrypt);

    @GET("ImageLibrary/GetImageLibraryList")
    Call<GetImagesForTopicRes> GetImagesForTopic(@Query("StatusCode") String StatusCode);


    @GET("Category/GetAllCategories")
    Call<GetAllCategoryData> GetAllCategories(@Query("StatusCode") String StatusCode);

    @POST("Category/GetSubCategoryList")
    Call<GetAllSubCategoryData> GetSubCategoryList(@Body CategoryReqData input);


    @GET("ljiapi/Common/GetCategoryList")
    Call<GetCategoryRes> GetCategoryList();




    @GET
    Call<AdButlerImgResponse> getImageAd(@Url String url);

    @GET("ljiapi/User/SearchSuburb")
    Call<SearchSuburbRes> SearchSuburb(@Query("PostalCodeORLocality") String PostalCodeORLocality);

    @GET("ljiapi/v2.0/Enterprise/ValidateEnterpriseCode")
    Call<ValidateEnterprise> ValidateEnterpriseCode(@Query("EnterpriseCode") String EnterpriseCode);


    @GET("lji/LJI20Contents/5d2d93a6d7f2ac176031819b.json")
    Call<TimeLineListResponse> getTimlineListData();



    @GET("User/GetUserProfile")
    Call<ProfileDataResponse> getProfileData(@Query("UserID") String UserID);

    @GET("Advertisement/GetEnterpriseAds")
    Call<GetEnterpriseAdsData> GetEnterpriseAds(@Query("EnterpriseID") String EnterpriseID);

}
