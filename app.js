function loadState() {
  try {
    const raw = localStorage.getItem('antiProcrastinationState');
    const parsed = raw ? JSON.parse(raw) : {};
    return {
      activeTasks: Array.isArray(parsed.activeTasks) ? parsed.activeTasks : [],
      doneTasks: Array.isArray(parsed.doneTasks) ? parsed.doneTasks : [],
      focusCount: Number.isFinite(parsed.focusCount) ? parsed.focusCount : 0,
      habits: Array.isArray(parsed.habits) ? parsed.habits : []
    };
  } catch (e) {
    return { activeTasks: [], doneTasks: [], focusCount: 0, habits: [] };
  }
}

const state = loadState();
const el = (id) => document.getElementById(id);

const activeTasks = el('activeTasks');
const doneTasksList = el('doneTasksList');
const habitBody = el('habitBody');
const timerValue = el('timerValue');
const modeSelect = el('modeSelect');

let timerId = null;
let remaining = Number(modeSelect.value);

function save() {
  localStorage.setItem('antiProcrastinationState', JSON.stringify(state));
}

function formatTime(sec) {
  const m = String(Math.floor(sec / 60)).padStart(2, '0');
  const s = String(sec % 60).padStart(2, '0');
  return `${m}:${s}`;
}

function updateStats() {
  const total = state.activeTasks.length + state.doneTasks.length;
  const done = state.doneTasks.length;
  const progress = total === 0 ? 0 : Math.floor((done / total) * 100);

  el('totalTasks').textContent = total;
  el('doneTasks').textContent = done;
  el('focusCount').textContent = state.focusCount;
  el('dayProgress').style.width = `${progress}%`;
  el('progressText').textContent = `Прогресс дня: ${progress}%`;
}

function createTaskLine(text, doneAt) {
  const wrapper = document.createElement('span');
  wrapper.textContent = doneAt ? `✅ ${text} (${doneAt})` : text;
  return wrapper;
}

function renderTasks() {
  activeTasks.innerHTML = '';
  doneTasksList.innerHTML = '';

  state.activeTasks.forEach((task, index) => {
    const li = document.createElement('li');
    li.append(createTaskLine(task.text));

    const actions = document.createElement('div');
    actions.className = 'task-actions';

    const doneBtn = document.createElement('button');
    doneBtn.className = 'done';
    doneBtn.type = 'button';
    doneBtn.textContent = 'Готово';
    doneBtn.onclick = () => {
      state.doneTasks.push({
        text: task.text,
        doneAt: new Date().toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })
      });
      state.activeTasks.splice(index, 1);
      save();
      render();
    };

    const delBtn = document.createElement('button');
    delBtn.className = 'delete';
    delBtn.type = 'button';
    delBtn.textContent = 'Удалить';
    delBtn.onclick = () => {
      state.activeTasks.splice(index, 1);
      save();
      render();
    };

    actions.append(doneBtn, delBtn);
    li.append(actions);
    activeTasks.append(li);
  });

  state.doneTasks.forEach((task, index) => {
    const li = document.createElement('li');
    li.append(createTaskLine(task.text, task.doneAt || ''));

    const delBtn = document.createElement('button');
    delBtn.className = 'delete';
    delBtn.type = 'button';
    delBtn.textContent = 'Удалить';
    delBtn.onclick = () => {
      state.doneTasks.splice(index, 1);
      save();
      render();
    };

    const actions = document.createElement('div');
    actions.className = 'task-actions';
    actions.append(delBtn);
    li.append(actions);

    doneTasksList.append(li);
  });
}

function renderHabits() {
  habitBody.innerHTML = '';
  state.habits.forEach((habit, index) => {
    const tr = document.createElement('tr');

    const name = document.createElement('td');
    name.textContent = habit.name;

    const streak = document.createElement('td');
    streak.textContent = String(habit.streak);

    const lastDate = document.createElement('td');
    lastDate.textContent = habit.lastDate || '—';

    const actions = document.createElement('td');

    const markBtn = document.createElement('button');
    markBtn.type = 'button';
    markBtn.textContent = 'Отметить';
    markBtn.onclick = () => {
      const today = new Date().toISOString().slice(0, 10);
      if (habit.lastDate !== today) {
        habit.streak += 1;
        habit.lastDate = today;
        save();
        renderHabits();
      }
    };

    const delBtn = document.createElement('button');
    delBtn.type = 'button';
    delBtn.className = 'delete small';
    delBtn.textContent = 'Удалить';
    delBtn.onclick = () => {
      state.habits.splice(index, 1);
      save();
      renderHabits();
    };

    actions.append(markBtn, delBtn);
    tr.append(name, streak, lastDate, actions);
    habitBody.append(tr);
  });
}

function renderTimer() {
  timerValue.textContent = formatTime(Math.max(remaining, 0));
}

function resetTimerFromMode() {
  remaining = Number(modeSelect.value);
  renderTimer();
}

function addTask() {
  const input = el('taskInput');
  const text = input.value.trim();
  if (!text) return;
  state.activeTasks.push({
    text: `${text} (${new Date().toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })})`
  });
  input.value = '';
  save();
  render();
}

function addHabit() {
  const input = el('habitInput');
  const name = input.value.trim();
  if (!name) return;
  state.habits.push({ name, streak: 0, lastDate: '' });
  input.value = '';
  save();
  renderHabits();
}

el('addTaskBtn').onclick = addTask;
el('addHabitBtn').onclick = addHabit;

el('taskInput').addEventListener('keydown', (event) => {
  if (event.key === 'Enter') addTask();
});

el('habitInput').addEventListener('keydown', (event) => {
  if (event.key === 'Enter') addHabit();
});

el('startTimer').onclick = () => {
  if (timerId) return;
  timerId = setInterval(() => {
    remaining -= 1;
    renderTimer();
    if (remaining <= 0) {
      clearInterval(timerId);
      timerId = null;
      if (Number(modeSelect.value) === 1500) {
        state.focusCount += 1;
        save();
        updateStats();
      }
      alert('Сессия завершена! Отличная работа 🚀');
      resetTimerFromMode();
    }
  }, 1000);
};

el('pauseTimer').onclick = () => {
  clearInterval(timerId);
  timerId = null;
};

el('resetTimer').onclick = () => {
  clearInterval(timerId);
  timerId = null;
  resetTimerFromMode();
};

el('clearDataBtn').onclick = () => {
  clearInterval(timerId);
  timerId = null;
  state.activeTasks = [];
  state.doneTasks = [];
  state.habits = [];
  state.focusCount = 0;
  save();
  resetTimerFromMode();
  render();
};

modeSelect.onchange = () => {
  clearInterval(timerId);
  timerId = null;
  resetTimerFromMode();
};

function render() {
  renderTasks();
  renderHabits();
  updateStats();
}

el('todayDate').textContent = `Сегодня: ${new Date().toLocaleDateString('ru-RU')}`;
resetTimerFromMode();
render();
