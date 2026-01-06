FROM node:20-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

# ビルド時に外部から引数として受け取る
ARG NEXT_PUBLIC_LIFF_ID
# 受け取った値を環境変数として設定する（Next.jsのビルドで参照される）
ENV NEXT_PUBLIC_LIFF_ID=$NEXT_PUBLIC_LIFF_ID

RUN npm run build

FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production

COPY --from=builder /app ./

EXPOSE 3000

CMD ["npm", "run", "start"]
