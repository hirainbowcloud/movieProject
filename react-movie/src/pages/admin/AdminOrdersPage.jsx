import { Fragment, useEffect, useState } from 'react';
import { request, requestPage } from '../../api.js';
import Pager from '../../components/admin/Pager.jsx';

const PAGE_SIZE = 5;   // 每頁幾筆；超過 1 頁才會出現分頁按鈕，自訂

// 訂單查詢：/admin/orders（只能查詢，不能修改、刪除）
// 清單：GET /api/orders/summary?username=&orderId=&page=&size=
// 展開看票：GET /api/orders/{id}（點開那一筆才去查）
function AdminOrdersPage() {
  const [usernameInput, setUsernameInput] = useState('');
  const [orderInput, setOrderInput] = useState('');
  const [filters, setFilters] = useState({ username: '', orderId: '' });   // 按下查詢才生效
  const [page, setPage] = useState(1);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const [openId, setOpenId] = useState(null);       // 展開看票的訂單
  const [details, setDetails] = useState({});       // 已查過的訂單明細：{ 訂單編號: 訂單 }

  useEffect(() => {
    // 組查詢參數：有填才加上去
    const params = new URLSearchParams({ page, size: PAGE_SIZE });
    if (filters.username) params.set('username', filters.username);
    if (filters.orderId) params.set('orderId', filters.orderId);

    setData(null);
    requestPage(`/api/orders/summary?${params}`)
      .then((d) => { setData(d); setError(''); })
      .catch((err) => setError(err.message));
  }, [filters, page]);

  const handleSearch = (e) => {
    e.preventDefault();
    setOpenId(null);
    setFilters({ username: usernameInput.trim(), orderId: orderInput.trim() });
    setPage(1);
  };

  const handleClear = () => {
    setUsernameInput('');
    setOrderInput('');
    setFilters({ username: '', orderId: '' });
    setOpenId(null);
    setPage(1);
    setError('');
  };

  // 點一列：展開 / 收合；第一次展開時才去查這筆訂單的票
  const toggle = async (orderId) => {
    if (openId === orderId) {
      setOpenId(null);
      return;
    }
    setOpenId(orderId);
    if (details[orderId]) return;   // 查過了就不用再查
    try {
      const o = await request(`/api/orders/${orderId}`);
      setDetails((prev) => ({ ...prev, [orderId]: o }));
    } catch (err) {
      setDetails((prev) => ({ ...prev, [orderId]: { error: err.message } }));
    }
  };

  const rows = data?.content || [];
  const fmtTime = (t) => (t ? t.replace('T', ' ').slice(0, 16) : '');

  return (
    <div className="admin-page">
      <div className="page-head">
        <div>
          <h1>訂單查詢</h1>
          <p>查看全部訂單、依會員帳號或訂單編號查詢，點一列可以看這筆訂單的票</p>
        </div>
      </div>

      <form className="toolbar" onSubmit={handleSearch}>
        <label className="field inline">
          <span>會員帳號</span>
          <input value={usernameInput} onChange={(e) => setUsernameInput(e.target.value)} />
        </label>
        <label className="field inline">
          <span>訂單編號</span>
          <input value={orderInput} onChange={(e) => setOrderInput(e.target.value)} type="number" min="1" />
        </label>
        <button className="btn btn-primary btn-sm">查詢</button>
        <button type="button" className="btn btn-ghost btn-sm" onClick={handleClear}>清除</button>
      </form>

      {error && <div className="alert">{error}</div>}

      <div className="panel">
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>訂單</th>
                <th>會員</th>
                <th>電影</th>
                <th>影廳</th>
                <th>場次日期</th>
                <th>場次時間</th>
                <th>張數</th>
                <th className="right">實收</th>
                <th>下單時間</th>
              </tr>
            </thead>
            <tbody>
              {data === null && !error && <tr><td colSpan="9" className="empty">載入中...</td></tr>}
              {data && rows.length === 0 && <tr><td colSpan="9" className="empty">沒有訂單</td></tr>}
              {rows.map((o) => {
                const d = details[o.orderId];
                return (
                  <Fragment key={o.orderId}>
                    <tr className="clickable" onClick={() => toggle(o.orderId)}>
                      <td className="mono strong">
                        <span className="caret">{openId === o.orderId ? '▾' : '▸'}</span> #{o.orderId}
                      </td>
                      <td className="mono">{o.username}</td>
                      <td className="strong">{o.title}</td>
                      <td className="nowrap">{o.hallName}</td>
                      <td className="mono nowrap">{o.showDate}</td>
                      <td className="mono">{o.startTime?.slice(0, 5)}</td>
                      <td>{o.ticketCount} 張</td>
                      <td className="right strong">NT${o.totalAmount}</td>
                      <td className="mono muted nowrap">{fmtTime(o.orderTime)}</td>
                    </tr>
                    {openId === o.orderId && (
                      <tr className="detail-row">
                        <td colSpan="9">
                          {!d && <div className="muted">載入中...</div>}
                          {d?.error && <div className="alert">{d.error}</div>}
                          {d && !d.error && (
                            <>
                              <div className="ticket-chips">
                                {d.tickets.map((t) => (
                                  <span key={t.ticketId} className="ticket-chip">
                                    {t.rowNo} 排 {t.seatNo} 號・{t.typeName}・NT${t.price}
                                  </span>
                                ))}
                              </div>
                              <div className="detail-sum">
                                原價 NT${d.originalAmount}　折扣 NT${d.discountAmount}　實收 <strong>NT${d.totalAmount}</strong>
                              </div>
                            </>
                          )}
                        </td>
                      </tr>
                    )}
                  </Fragment>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {data && data.totalElements > 0 && (
        <p className="form-hint">
          共 {data.totalElements} 筆，第 {page} / {data.totalPages} 頁
        </p>
      )}
      {data && <Pager page={page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}

export default AdminOrdersPage;
