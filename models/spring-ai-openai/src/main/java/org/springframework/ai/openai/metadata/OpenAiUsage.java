/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.openai.metadata;

import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.util.Assert;

/**
 * {@link Usage} implementation for {@literal OpenAI}.
 *
 * @author John Blum
 * @author Thomas Vitale
 * @author David Frizelle
 * @author Christian Tzolov
 * @since 0.7.0
 * @see <a href=
 * "https://platform.openai.com/docs/api-reference/completions/object">Completion
 * Object</a>
 */
public class OpenAiUsage implements Usage {

	private final OpenAiApi.Usage usage;

	protected OpenAiUsage(OpenAiApi.Usage usage) {
		Assert.notNull(usage, "OpenAI Usage must not be null");
		this.usage = usage;
	}

	public static OpenAiUsage from(OpenAiApi.Usage usage) {
		return new OpenAiUsage(usage);
	}

	protected OpenAiApi.Usage getUsage() {
		return this.usage;
	}

	@Override
	public Long getPromptTokens() {
		Integer promptTokens = getUsage().promptTokens();
		return promptTokens != null ? promptTokens.longValue() : 0;
	}

	@Override
	public Long getGenerationTokens() {
		Integer generationTokens = getUsage().completionTokens();
		return generationTokens != null ? generationTokens.longValue() : 0;
	}

	@Override
	public Long getTotalTokens() {
		Integer totalTokens = getUsage().totalTokens();
		if (totalTokens != null) {
			return totalTokens.longValue();
		}
		else {
			return getPromptTokens() + getGenerationTokens();
		}
	}

	/**
	 * @deprecated Use {@link #getPromptTokensDetails()} instead.
	 */
	@Deprecated
	public Long getPromptTokensDetailsCachedTokens() {
		OpenAiApi.Usage.PromptTokensDetails promptTokenDetails = getUsage().promptTokensDetails();
		Integer cachedTokens = promptTokenDetails != null ? promptTokenDetails.cachedTokens() : null;
		return cachedTokens != null ? cachedTokens.longValue() : 0;
	}

	public PromptTokensDetails getPromptTokensDetails() {
		var details = getUsage().promptTokensDetails();
		if (details == null) {
			return new PromptTokensDetails(0, 0);
		}
		return new PromptTokensDetails(valueOrZero(details.audioTokens()), valueOrZero(details.cachedTokens()));
	}

	/**
	 * @deprecated Use {@link #getCompletionTokenDetails()} instead.
	 */
	@Deprecated
	public Long getReasoningTokens() {
		OpenAiApi.Usage.CompletionTokenDetails completionTokenDetails = getUsage().completionTokenDetails();
		Integer reasoningTokens = completionTokenDetails != null ? completionTokenDetails.reasoningTokens() : null;
		return reasoningTokens != null ? reasoningTokens.longValue() : 0;
	}

	/**
	 * @deprecated Use {@link #getCompletionTokenDetails()} instead.
	 */
	@Deprecated
	public Long getAcceptedPredictionTokens() {
		OpenAiApi.Usage.CompletionTokenDetails completionTokenDetails = getUsage().completionTokenDetails();
		Integer acceptedPredictionTokens = completionTokenDetails != null
				? completionTokenDetails.acceptedPredictionTokens() : null;
		return acceptedPredictionTokens != null ? acceptedPredictionTokens.longValue() : 0;
	}

	/**
	 * @deprecated Use {@link #getCompletionTokenDetails()} instead.
	 */
	@Deprecated
	public Long getAudioTokens() {
		OpenAiApi.Usage.CompletionTokenDetails completionTokenDetails = getUsage().completionTokenDetails();
		Integer audioTokens = completionTokenDetails != null ? completionTokenDetails.audioTokens() : null;
		return audioTokens != null ? audioTokens.longValue() : 0;
	}

	/**
	 * @deprecated Use {@link #getCompletionTokenDetails()} instead.
	 */
	@Deprecated
	public Long getRejectedPredictionTokens() {
		OpenAiApi.Usage.CompletionTokenDetails completionTokenDetails = getUsage().completionTokenDetails();
		Integer rejectedPredictionTokens = completionTokenDetails != null
				? completionTokenDetails.rejectedPredictionTokens() : null;
		return rejectedPredictionTokens != null ? rejectedPredictionTokens.longValue() : 0;
	}

	public CompletionTokenDetails getCompletionTokenDetails() {
		var details = getUsage().completionTokenDetails();
		if (details == null) {
			return new CompletionTokenDetails(0, 0, 0, 0);
		}
		return new CompletionTokenDetails(valueOrZero(details.reasoningTokens()),
				valueOrZero(details.acceptedPredictionTokens()), valueOrZero(details.audioTokens()),
				valueOrZero(details.rejectedPredictionTokens()));
	}

	@Override
	public String toString() {
		return getUsage().toString();
	}

	private int valueOrZero(Integer value) {
		return value != null ? value : 0;
	}

	public record PromptTokensDetails(// @formatter:off
		Integer audioTokens,
		Integer cachedTokens) {
	}

	public record CompletionTokenDetails(
		Integer reasoningTokens,
		Integer acceptedPredictionTokens,
		Integer audioTokens,
		Integer rejectedPredictionTokens) { // @formatter:on
	}

}
