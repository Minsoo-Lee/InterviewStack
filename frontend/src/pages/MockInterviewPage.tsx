import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { apiClient } from '../api/client';
import type { InterviewMessage, InterviewSessionDetail } from '../types';
import './MockInterviewPage.css';

// TODO: GET /api/interviews/{id}, POST /api/interviews/{id}/messages, POST /api/interviews/{id}/end 연동
export function MockInterviewPage() {
  const { id } = useParams<{ id: string }>();
  const [session, setSession] = useState<InterviewSessionDetail | null>(null);
  const [draft, setDraft] = useState('');
  const [sending, setSending] = useState(false);

  useEffect(() => {
    if (!id) return;
    apiClient
      .get<InterviewSessionDetail>(`/api/interviews/${id}`)
      .then((res) => setSession(res.data))
      .catch(() => setSession(null));
  }, [id]);

  async function handleSend() {
    if (!id || !draft.trim()) return;
    setSending(true);
    try {
      const res = await apiClient.post<InterviewMessage>(`/api/interviews/${id}/messages`, {
        content: draft,
      });
      setSession((prev) => (prev ? { ...prev, messages: [...prev.messages, res.data] } : prev));
      setDraft('');
    } finally {
      setSending(false);
    }
  }

  async function handleEndSession() {
    if (!id) return;
    const res = await apiClient.post<InterviewSessionDetail>(`/api/interviews/${id}/end`);
    setSession(res.data);
  }

  return (
    <div className="mock-interview">
      <aside className="mock-interview__sidebar">
        <div>
          <div className="label">세션 정보</div>
          <p>이력서/JD 기반 맞춤 질문 세션</p>
        </div>

        <div>
          <div className="label">진행 상황</div>
          <p>{session ? `${session.messages.filter((m) => m.role === 'ai').length}개 질문 진행` : '-'}</p>
        </div>

        <div>
          <div className="label">다룬 주제</div>
          <div className="tag-list">
            {(session?.topicsCovered ?? []).map((t) => (
              <span key={t} className="tag">
                {t}
              </span>
            ))}
          </div>
        </div>

        <button type="button" className="btn-danger-ghost" onClick={handleEndSession}>
          세션 종료 &amp; 리포트 보기
        </button>
      </aside>

      <section className="mock-interview__chat">
        {session?.status === 'completed' && session.report ? (
          <div className="mock-interview__report">
            <h2>세션 종합 리포트</h2>
            <p>{session.report}</p>
          </div>
        ) : (
          <>
            <div className="mock-interview__messages">
              {(session?.messages ?? []).map((m) => (
                <div key={m.id} className={`bubble bubble--${m.role}`}>
                  {m.content}
                </div>
              ))}
            </div>

            <div className="mock-interview__input">
              <input
                type="text"
                placeholder="답변을 입력하세요..."
                value={draft}
                onChange={(e) => setDraft(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleSend()}
              />
              <button type="button" disabled={sending || !draft.trim()} onClick={handleSend}>
                전송
              </button>
            </div>
          </>
        )}
      </section>
    </div>
  );
}
