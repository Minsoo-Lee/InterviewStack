import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { apiClient } from '../api/client';
import type { AnswerWithFeedback, Question } from '../types';

// TODO: GET /api/questions/{id}, POST /api/questions/{id}/answers 연동
export function QuestionSolvePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [question, setQuestion] = useState<Question | null>(null);
  const [content, setContent] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!id) return;
    apiClient
      .get<Question>(`/api/questions/${id}`)
      .then((res) => setQuestion(res.data))
      .catch(() => setQuestion(null));
  }, [id]);

  async function handleSubmit() {
    if (!id || !content.trim()) return;
    setSubmitting(true);
    try {
      const res = await apiClient.post<AnswerWithFeedback>(`/api/questions/${id}/answers`, {
        content,
      });
      navigate(`/answers/${res.data.answer.id}/feedback`, { state: res.data });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="page">
      <h1>질문 풀이</h1>

      {question ? (
        <>
          <p className="page__tag">
            {question.category} · {question.subTopic} · {question.difficulty}
          </p>
          <p className="page__question">{question.content}</p>
        </>
      ) : (
        <p>불러오는 중...</p>
      )}

      <textarea
        className="answer-textarea"
        rows={10}
        placeholder="답변을 입력하세요..."
        value={content}
        onChange={(e) => setContent(e.target.value)}
      />

      <button type="button" disabled={submitting || !content.trim()} onClick={handleSubmit}>
        {submitting ? '첨삭 중...' : '제출하고 첨삭받기'}
      </button>
    </div>
  );
}
