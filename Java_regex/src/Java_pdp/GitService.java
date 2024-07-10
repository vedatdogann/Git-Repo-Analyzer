/**
*
* @author VEDAT DOGAN - vedat202dogan@gmail.com
* @since  07.04.2024
* <p>
*         1-B Subesi
* </p>
*/

package Java_pdp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitService {

	private String repository;  				//Repository klonla
	private Path cloneDirectory;  		
	private Git git;
	
	public GitService(final String aRepository, final String aCloneDirectory) throws IOException{
		this.repository=aRepository;
		this.cloneDirectory=Paths.get(aCloneDirectory).toAbsolutePath().normalize();
		try {
			this.git=Git.init().call();
		}catch(GitAPIException e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	public void cloneRepo() {
		try {
			File testFile=this.cloneDirectory.toFile();
			this.git.cloneRepository().setURI(this.repository).setDirectory(this.cloneDirectory.toFile()).call();
		}catch(GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public void DisplayCurrentBranches() {
		
	}
}
