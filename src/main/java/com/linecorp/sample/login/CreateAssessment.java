package com.linecorp.sample.login;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;
import com.google.recaptchaenterprise.v1.Assessment;
import com.google.recaptchaenterprise.v1.CreateAssessmentRequest;
import com.google.recaptchaenterprise.v1.Event;
import com.google.recaptchaenterprise.v1.ProjectName;
import com.google.recaptchaenterprise.v1.RiskAnalysis.ClassificationReason;
import java.io.IOException;

public class CreateAssessment {

  public static void main(String[] args) throws IOException {
    // TODO: サンプルを実行する前に、トークンと reCAPTCHA アクション変数を置き換えます。
    String projectID = "roomtest-466307";
    String recaptchaKey = "6Lc3O4srAAAAAIEm0U3chQW1ZCfIlo4QVM53HtRT";
    String token = "a616da02cd5a6bafdbed369553247364e1b599bb";
    String recaptchaAction = "action-name";

    createAssessment(projectID, recaptchaKey, token, recaptchaAction);
  }

  /**
   * 評価を作成して UI アクションのリスクを分析する。
   *
   * @param projectID : Google Cloud プロジェクト ID
   * @param recaptchaKey : サイト / アプリに関連付けられた reCAPTCHA キー
   * @param token : クライアントから取得した生成トークン。
   * @param recaptchaAction : トークンに対応するアクション名。
   */
  public static void createAssessment(
      String projectID, String recaptchaKey, String token, String recaptchaAction)
      throws IOException {
    // reCAPTCHA クライアントを作成する。
    // TODO: クライアント生成コードをキャッシュに保存するか（推奨）、メソッドを終了する前に client.close() を呼び出す。
    try (RecaptchaEnterpriseServiceClient client = RecaptchaEnterpriseServiceClient.create()) {

      // 追跡するイベントのプロパティを設定する。
      Event event = Event.newBuilder().setSiteKey(recaptchaKey).setToken(token).build();

      // 評価リクエストを作成する。
      CreateAssessmentRequest createAssessmentRequest =
          CreateAssessmentRequest.newBuilder()
              .setParent(ProjectName.of(projectID).toString())
              .setAssessment(Assessment.newBuilder().setEvent(event).build())
              .build();

      Assessment response = client.createAssessment(createAssessmentRequest);

      // トークンが有効かどうかを確認する。
      if (!response.getTokenProperties().getValid()) {
        System.out.println(
            "The CreateAssessment call failed because the token was: "
                + response.getTokenProperties().getInvalidReason().name());
        return;
      }

      // 想定どおりのアクションが実行されたかどうかを確認する。
      if (!response.getTokenProperties().getAction().equals(recaptchaAction)) {
        System.out.println(
            "The action attribute in reCAPTCHA tag is: "
                + response.getTokenProperties().getAction());
        System.out.println(
            "The action attribute in the reCAPTCHA tag "
                + "does not match the action ("
                + recaptchaAction
                + ") you are expecting to score");
        return;
      }

      // リスクスコアと理由を取得する。
      // 評価の解釈の詳細については、以下を参照:
      // https://cloud.google.com/recaptcha-enterprise/docs/interpret-assessment
      for (ClassificationReason reason : response.getRiskAnalysis().getReasonsList()) {
        System.out.println(reason);
      }

      float recaptchaScore = response.getRiskAnalysis().getScore();
      System.out.println("The reCAPTCHA score is: " + recaptchaScore);

      // 評価名（id）を取得する。これを使用して、評価にアノテーションを付ける。
      String assessmentName = response.getName();
      System.out.println(
          "Assessment name: " + assessmentName.substring(assessmentName.lastIndexOf("/") + 1));
    }
  }
}