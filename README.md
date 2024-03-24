TalkDoc -- 基于文档的交谈式AI
--------------------
## 使用截图

<img width="2559" alt="SCR-20240324-pzsn" src="https://github.com/milan-chen/talk-doc/assets/30249185/7159d9ea-b2ed-4f62-a3eb-9b38e85e5477">

## 实现原理

<img width="938" alt="SCR-20240324-qhif" src="https://github.com/milan-chen/talk-doc/assets/30249185/d1418814-91ed-490e-9586-02ca766088c5">


## 更多介绍
- Java语言对OpenAI、Milvus的调用封装
  - gpt-3.5-turbo大语言模型
  - text-embedding-ada-002向量化
  - milvus向量数据库
  - 可以扩展，对接国内大预言模型、向量数据库

- 两种聊天模式
  - 自由模式：类似chatgpt自由聊天
  - 专业模式：默认的模式；专注于文档内容的问答

- 流式输出打字机效果

- 上下文对话记忆

## 如何运行

```
openai.host=支持代理地址
openai.key=你的授权key

milvus.url=zilliz云数据库地址
milvus.token=你的授权token
```

  

    
