/**
 * User Onboarding Manager for Shoe Tracker.
 * Manages first-visit name prompt and localStorage identity.
 */
(function() {
  window.ShoeTracker = window.ShoeTracker || {};

  class OnboardingManager {
    constructor() {
      this.modalEl = null;
      this.inputEl = null;
      this.errorEl = null;
      this.userChipEl = null;
      this.nameListeners = [];
    }

    init() {
      this.modalEl = document.getElementById('onboardingModal');
      this.inputEl = document.getElementById('onboardingInput');
      this.errorEl = document.getElementById('onboardingError');
      this.userChipEl = document.getElementById('userChip');
      const formEl = document.getElementById('onboardingForm');

      if (formEl) {
        formEl.addEventListener('submit', (e) => {
          e.preventDefault();
          this.handleSave();
        });
      }

      if (this.userChipEl) {
        this.userChipEl.addEventListener('click', () => {
          this.show(this.getUserId() || '');
        });
      }

      const currentUserId = this.getUserId();
      if (!currentUserId) {
        this.show('');
      } else {
        this.updateHeaderChip(currentUserId);
      }
    }

    getUserId() {
      return localStorage.getItem('user_id');
    }

    show(defaultVal = '') {
      if (!this.modalEl) return;
      this.inputEl.value = defaultVal;
      this.errorEl.textContent = '';
      this.inputEl.classList.remove('input-error');
      this.modalEl.classList.add('active');
      setTimeout(() => this.inputEl.focus(), 100);
    }

    hide() {
      if (!this.modalEl) return;
      this.modalEl.classList.remove('active');
    }

    handleSave() {
      const raw = this.inputEl.value || '';
      const trimmed = raw.trim();

      if (!trimmed) {
        this.errorEl.textContent = 'Please enter your name.';
        this.inputEl.classList.add('input-error');
        this.inputEl.focus();
        return;
      }

      if (trimmed.length > 64) {
        this.errorEl.textContent = 'Name must be 64 characters or fewer.';
        this.inputEl.classList.add('input-error');
        this.inputEl.focus();
        return;
      }

      localStorage.setItem('user_id', trimmed);
      this.updateHeaderChip(trimmed);
      this.hide();

      this.nameListeners.forEach((fn) => fn(trimmed));
    }

    updateHeaderChip(name) {
      if (this.userChipEl) {
        this.userChipEl.textContent = `👤 ${name}`;
        this.userChipEl.style.display = 'inline-flex';
      }
    }

    onNameChange(fn) {
      this.nameListeners.push(fn);
      const current = this.getUserId();
      if (current) fn(current);
    }
  }

  window.ShoeTracker.onboarding = new OnboardingManager();
})();
