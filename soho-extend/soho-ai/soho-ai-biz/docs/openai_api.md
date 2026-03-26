# responses

## request

```json
{
  "model": "gpt-5.4",
  "instructions": "你是一个简洁的编码助手",
  "input": [
    {
      "role": "user",
      "content": [
        {
          "type": "input_text",
          "text": "写一个 Python hello world"
        }
      ]
    }
  ],
  "tools": [
    {
      "type": "function",
      "name": "get_weather",
      "description": "获取天气",
      "parameters": {
        "type": "object",
        "properties": {
          "city": {
            "type": "string"
          }
        },
        "required": [
          "city"
        ]
      },
      "strict": true
    }
  ],
  "tool_choice": "auto",
  "parallel_tool_calls": true,
  "reasoning": {
    "effort": "medium",
    "summary": "auto"
  },
  "store": false,
  "stream": true,
  "include": [
    "reasoning.encrypted_content"
  ],
  "service_tier": "priority",
  "prompt_cache_key": "demo-cache-key",
  "text": {
    "format": "text",
    "verbosity": "low"
  },
  "temperature": 0.7,
  "top_p": 0.9,
  "max_output_tokens": 512
}
```
