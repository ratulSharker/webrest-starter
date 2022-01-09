package com.webrest.common.service;

import java.util.List;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

	private JavaMailSender javaMailSender;
	private TemplateEngine templateEngine;

	public EmailService(JavaMailSender javaMailSender,
			@Qualifier(value = "emailTemplateEngine") TemplateEngine templateEngine) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
	}

	public void sendPasswordRecoveryEmail(String toEmailAddr, String redirectURL) {
		final Context ctx = new Context();
		ctx.setVariable("passwordResetUrl", redirectURL);
		final String htmlContent = this.templateEngine.process("mail/password-recovery-template", ctx);

		sendEmail(List.of(toEmailAddr), "Recover Company Admin panel password", htmlContent);
	}

	private void sendEmail(List<String> recipients, String subject, String body) {
		MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() { // https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/mail.html

			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
				helper.setTo(recipients.toArray(new String[recipients.size()])); // https://stackoverflow.com/a/9863752
				helper.setSubject(subject);
				helper.setText(body, true);
			}
		};

		javaMailSender.send(mimeMessagePreparator);
	}
}
