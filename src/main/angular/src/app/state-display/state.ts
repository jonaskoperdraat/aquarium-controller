export class AquariumState {

  constructor(
    public tl: boolean = false,
    public led1: Color = new Color(),
    public led2: Color = new Color()) {
  }

  toString(): string {
    return `AquariumState {tl: ${this.tl}, led1: ${this.led1}, led2: ${this.led2}}`;
  }
}

export class Color {

  constructor(
    public r: number = 0,
    public g: number = 0,
    public b: number = 0){}

  toString(): string {
    return `Color <${this.r}, ${this.g}, ${this.b}>`;
  }
}
