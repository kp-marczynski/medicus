export interface IFile {
  id?: number;
  contentContentType?: string;
  content?: any;
}

export class File implements IFile {
  constructor(public id?: number, public contentContentType?: string, public content?: any) {}
}
