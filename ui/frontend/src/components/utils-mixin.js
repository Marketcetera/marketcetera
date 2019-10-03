import '@polymer/polymer/polymer-element.js';
import { afterNextRender } from '@polymer/polymer/lib/utils/render-status.js';
/**
 * Adds the scroll-shadow attribute if there is a `#main` element with scrollsize
 * larger than its height, and there is hidden content at the bottom.
 */
/* @polymerMixin */
window.ScrollShadowMixin = subclass => class extends subclass {
  static get properties() {
    return {
      noScroll: {
        type: Boolean,
        reflectToAttribute: true
      },
      _main: Element,
      _boundContentScroll: Function
    };
  }

  ready() {
    super.ready();
    afterNextRender(this, () => {
      this._main = this.root.querySelector('#main');
      if (this._main) {
        this._main.addEventListener('scroll', this._contentScroll.bind(this));
        this._contentScroll();
      }
    });
    this._boundContentScroll = this._contentScroll.bind(this);
  }

  _contentScroll() {
    if (this._main) {
      this.noScroll = this._main.scrollHeight - this._main.scrollTop == this._main.clientHeight;
    }
  }
};
