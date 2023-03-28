import {LitElement, html, css} from 'lit';

export class DemoTitle extends LitElement {

    static styles = css`
      h1 {
        font-family: Pacifico, fantasy;
        font-size: 60px;
        font-style: normal;
        font-variant: normal;
        font-weight: 700;
        line-height: 26.4px;
        color: var(--main-highlight-text-color);
      }

      .title {
        text-align: center;
        padding: 1em;
      }
    `

    render() {
        return html`
            <div class="title">
                <h1>Instaquarm!</h1>
            </div>
        `
    }


}

customElements.define('demo-title', DemoTitle);

