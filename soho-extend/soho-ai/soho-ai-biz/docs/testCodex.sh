#!/usr/bin/env bash
set -euo pipefail

export OPENAI_API_KEY="eyJhbGciOiJSUzI1NiIsImtpZCI6IjE5MzQ0ZTY1LWJiYzktNDRkMS1hOWQwLWY5NTdiMDc5YmQwZSIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiaHR0cHM6Ly9hcGkub3BlbmFpLmNvbS92MSJdLCJjbGllbnRfaWQiOiJhcHBfRU1vYW1FRVo3M2YwQ2tYYVhwN2hyYW5uIiwiZXhwIjoxNzc1Mjk1OTM2LCJodHRwczovL2FwaS5vcGVuYWkuY29tL2F1dGgiOnsiY2hhdGdwdF9hY2NvdW50X2lkIjoiZTgwZTZkYWEtMzFmYy00YjlmLTg5MjctNmI3NjMxY2EyNGQ2IiwiY2hhdGdwdF9hY2NvdW50X3VzZXJfaWQiOiJ1c2VyLWI1eVpnU2RKd05PM005WnhNYzRCbTV2QV9fZTgwZTZkYWEtMzFmYy00YjlmLTg5MjctNmI3NjMxY2EyNGQ2IiwiY2hhdGdwdF9jb21wdXRlX3Jlc2lkZW5jeSI6Im5vX2NvbnN0cmFpbnQiLCJjaGF0Z3B0X3BsYW5fdHlwZSI6InRlYW0iLCJjaGF0Z3B0X3VzZXJfaWQiOiJ1c2VyLWI1eVpnU2RKd05PM005WnhNYzRCbTV2QSIsImxvY2FsaG9zdCI6dHJ1ZSwidXNlcl9pZCI6InVzZXItYjV5WmdTZEp3Tk8zTTlaeE1jNEJtNXZBIn0sImh0dHBzOi8vYXBpLm9wZW5haS5jb20vbWZhIjp7InJlcXVpcmVkIjoieWVzIn0sImh0dHBzOi8vYXBpLm9wZW5haS5jb20vcHJvZmlsZSI6eyJlbWFpbCI6InZ5cGJydzY2cDVAcHJpdmF0ZXJlbGF5LmFwcGxlaWQuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWV9LCJpYXQiOjE3NzQ0MzE5MzYsImlzcyI6Imh0dHBzOi8vYXV0aC5vcGVuYWkuY29tIiwianRpIjoiYTk3ZTU0MWEtNTVhMy00NDE3LTgxMzAtY2NjMjI3NTUxMWZmIiwibmJmIjoxNzc0NDMxOTM2LCJwd2RfYXV0aF90aW1lIjoxNzc0NDMxOTMyNDYzLCJzY3AiOlsib3BlbmlkIiwicHJvZmlsZSIsImVtYWlsIiwib2ZmbGluZV9hY2Nlc3MiLCJhcGkuY29ubmVjdG9ycy5yZWFkIiwiYXBpLmNvbm5lY3RvcnMuaW52b2tlIl0sInNlc3Npb25faWQiOiJhdXRoc2Vzc19CQjNXbktvOVJjV3pGM1htdERjbmZ5RXIiLCJzbCI6dHJ1ZSwic3ViIjoiYXBwbGV8MDAxNTE5LjE2MTQ1NjFmZTI3YjRiMjE4M2MzNzI3ZDU2NTQyMTQxLjE1MjEifQ.dwxUYR4dChRuMeOCH09QKECTEQV6hyh8BzKyAnUfYuyCjUUBygBFhstveV4PQlLW3StGenKHouuaXlz9bmOFgD-z68d1fc2-bn05CS9HWGOKPyb54GiUs59CYgINXO_r1ENTegf91YA2ZPwMNQZp2cKAriJOS4hZPEfy-vxTuTbE_zDmDpi7vUqtoovDzKagoawrU0nfFqF8DPVvN9LNVjRasfYmhILtO-XngjmiWqxzfuTCjNU84ESqR_HMJDMeixwA_Nq-Z1FZ3NbQDWc2EGrAvYcQRGSB4Rak-EmgYFb5rObbSBuR70IZtwanq9qL1qh9jVwXLSv2JdZaJgosc-i_EyQKOKdtyeGHgoUuOMoTD1vHLfwTd12uxaxGE3yoR6MjTv3lzd4Q7vCFXhL68WThmK3fEyUcW3O1V1fpFtPHIDvC1oyXWia_evFAGimPgvc9OMk4lj4rxyB_536qoXR-DZSxEDK2OWYotpCEnt99g9XKJPfmqxcPY_4t4Zf_nFBiYdBP18blnzPI_i1angZnzKgW6YdJqa5UHLuq1Tkpd2_jhPeeXb9yeY_YpzVUUNqf9fYmMjrGEQwQBROuGtBdROfaNDmwMHSohwVecxIQUDyKtEBPrLcVhZT2cvLKdw7Ve4jnp_YmKcGN4-VInWqrFOLAYOktItvi2ZkHcYE"

# store 一定是 false
# stream 一定是 true
curl https://chatgpt.com/backend-api/codex/responses \
  -x http://127.0.0.1:7890 \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-5.4",
    "instructions": "你是一个万能桌面助手。",
    "store": false,
    "stream": true,
    "input": [
      {
        "role": "user",
        "content": [
          {
            "type": "input_text",
            "text": "Reply with: test ok"
          }
        ]
      }
    ]
  }'
