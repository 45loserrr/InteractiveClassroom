// ============================================================
// 全组公共请求方法（原生 fetch，不依赖任何库）
// 用法：
//   const res = await myFetch('/api/classroom/list', 'GET');
//   const res = await myFetch('/api/checkin', 'POST', { classroomId: 1 });
// 返回：{ code, msg, data }，和后端 Result 一模一样。
// 特点：
//   1. 自动从 localStorage 取 token 放进请求头；
//   2. code 不是 200 时自动 alert 提示；
//   3. 401（没登录）自动清掉本地登录信息并跳回登录页。
// ============================================================

window.myFetch = async function (url, method, data) {
  method = method || 'GET';

  // 请求头：告诉后端我发的是 JSON
  const headers = { 'Content-Type': 'application/json' };

  // 自动带上登录凭证
  const token = localStorage.getItem('token');
  if (token) {
    headers['token'] = token;
  }

  const options = { method: method, headers: headers };

  // GET 请求一般不带 body，参数直接拼在 url 后面
  if (data && method !== 'GET') {
    options.body = JSON.stringify(data);
  }

  try {
    const response = await fetch(url, options);
    const result = await response.json();

    // 后端约定：只有 200 才算成功
    if (result.code !== 200) {
      alert(result.msg || '请求失败');

      // 没登录 / token 丢了：清空本地信息，回登录页
      if (result.code === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('realName');
        localStorage.removeItem('userId');
        location.href = '/login.html';
      }
    }

    return result;
  } catch (e) {
    alert('网络出错了：' + e);
    return { code: 500, msg: '网络出错', data: null };
  }
};
