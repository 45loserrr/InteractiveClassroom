// ============================================================
// 全站公共 JS（PM 维护，组员直接用，不要复制一份）
// 页面引入：<script src="/js/common.js"></script>
//
// 提供 4 个方法：
//   myFetch(url, method, data)  发请求，自动带 token、自动提示错误
//   checkLogin(role)            检查有没有登录，可以限制角色
//   fillUser()                  把当前用户名填到 id="userName" 的元素里
//   logout()                    退出登录，回登录页
// ============================================================

// ---------- 1. 退出登录 ----------
window.logout = function () {
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('realName');
  localStorage.removeItem('userId');
  location.replace('/index.html');
};

// ---------- 2. 请求封装 ----------
// 用法：
//   const res = await myFetch('/api/classroom/list', 'GET');
//   const res = await myFetch('/api/checkin', 'POST', { classroomId: 1 });
// 返回：{ code, msg, data }
window.myFetch = async function (url, method, data) {
  method = method || 'GET';

  var headers = { 'Content-Type': 'application/json' };
  var token = localStorage.getItem('token');
  if (token) {
    headers['token'] = token;
  }

  var options = { method: method, headers: headers };
  // GET 请求参数请直接拼在 url 后面，这里只给 POST/PUT 加 body
  if (data && method !== 'GET') {
    options.body = JSON.stringify(data);
  }

  try {
    var response = await fetch(url, options);
    var result = await response.json();

    if (result.code !== 200) {
      alert(result.msg || '请求失败');

      // token 没了或没登录：清空后回登录页
      if (result.code === 401) {
        logout();
      }
    }

    return result;
  } catch (e) {
    alert('网络出错了：' + e);
    return { code: 500, msg: '网络出错', data: null };
  }
};

// ---------- 3. 当前登录用户 ----------
window.getLoginUser = function () {
  return {
    token: localStorage.getItem('token') || '',
    role: localStorage.getItem('role') || '',
    realName: localStorage.getItem('realName') || '',
    userId: Number(localStorage.getItem('userId') || 0)
  };
};

// ---------- 4. 登录校验 ----------
// 用法：
//   var user = checkLogin();            // 只要登录就行
//   var user = checkLogin('teacher');   // 必须是老师
// 返回 null 表示没登录 / 角色不对，页面已经自动跳走了
window.checkLogin = function (needRole) {
  var user = getLoginUser();

  if (!user.token) {
    location.replace('/index.html');
    return null;
  }

  if (needRole && user.role !== needRole) {
    alert('这个页面不是给你用的');
    location.replace(user.role === 'teacher' ? '/teacher/' : '/student/');
    return null;
  }

  return user;
};

// ---------- 5. 把用户名显示到导航栏 ----------
window.fillUser = function () {
  var el = document.getElementById('userName');
  if (!el) {
    return;
  }
  var user = getLoginUser();
  el.innerText = user.realName || user.role || '未登录';
};
