import { useEffect, useState } from 'react';
import { useLocation, useParams } from 'react-router-dom';
import { apiClient } from '../api/client';
import type { AnswerWithFeedback } from '../types';

// TODO: GET /api/answers/{id} 연동 (질문 풀이에서 넘어온 경우 location.state로 초기 데이터 사용)
export function FeedbackResultPage() {
  const { answerId } = useParams<{ answerId: string }>();
  const location = useLocation();
  const stateData = (location.state as AnswerWithFeedback | undefined) ?? null;
  const [data, setData] = useState<AnswerWithFeedback | null>(stateData);
  const [loading, setLoading] = useState(!stateData);

  useEffect(() => {
    if (stateData || !answerId) return;
    apiClient
      .get<AnswerWithFeedback>(`/api/answers/${answerId}`)
      .then((res) => setData(res.data))
      .catch(() => setData(null))
      .finally(() => setLoading(false));
  }, [answerId, stateData]);

  if (loading) return <div className="page">불러오는 중...</div>;
  if (!data) return <div className="page page__empty">첨삭 결과를 찾을 수 없습니다.</div>;

  const { feedback } = data;

  return (
    <div className="page">
      <h1>첨삭 결과</h1>

      <p className="feedback-total">총점 {feedback.total} / 40</p>

      <ul className="feedback-scores">
        {Object.entries(feedback.scores).map(([axis, score]) => (
          <li key={axis}>
            {axis}: {score} / 10
          </li>
        ))}
      </ul>

      <section>
        <h2>총평</h2>
        <p>{feedback.summary}</p>
      </section>

      {feedback.references.length > 0 && (
        <section>
          <h2>근거자료</h2>
          <ul>
            {feedback.references.map((ref) => (
              <li key={ref.id}>
                {ref.sourceUrl ? <a href={ref.sourceUrl}>{ref.title}</a> : ref.title}
              </li>
            ))}
          </ul>
        </section>
      )}

      {feedback.isWeak && <p className="feedback-weak-badge">오답노트에 추가되었습니다</p>}
    </div>
  );
}
