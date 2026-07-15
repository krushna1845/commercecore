# AI Assistant Setup

To enable real AI responses for the shopping assistant, set the OpenAI API key in your environment before starting the backend.

PowerShell:

```powershell
$env:OPENAI_API_KEY="your-openai-api-key"
```

Then start the backend:

```powershell
cd commercecore
./mvnw spring-boot:run
```

If the key is not set, the assistant will continue to use the built-in fallback product suggestions.
