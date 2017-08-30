package parker.systems.mountpoint.usage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class FileUsageInfoGroupTest {
	
	@Test
	public void export_called_executesFunctionAgainstThis() {
		
		FileUsageInfoGroup cut = new FileUsageInfoGroup(Lists.newArrayList());
		
		cut.export((group) -> {
			assertThat(group).isEqualTo(cut);
			return "";
		});
	}
	
	@Test
	public void toJson_calledWithNoFiles_returnsEmptyJsonFileList() {
		
		FileUsageInfoGroup cut = new FileUsageInfoGroup(Lists.newArrayList());
		
		String EXPECTED = Joiner.on("\n").join(Lists.newArrayList(
				"{", 
				"  \"files\": []",
				"}"
				));
		
		String json = cut.toJson();
		assertThat(json).isEqualTo(EXPECTED);
	}
	
	@Test
	public void toJson_calledWithFiles_returnsJsonFileList() {
		
		FileUsageInfoGroup cut = new FileUsageInfoGroup(Lists.newArrayList(
				new FileUsageInfo("abc", 123),
				new FileUsageInfo("def", 456),
				new FileUsageInfo("ghi", 789)
				));
		
		String EXPECTED = Joiner.on("\n").join(Lists.newArrayList(
				"{", 
				"  \"files\": [",
				"    {",
				"      \"abc\": 123",
				"    },",
				"    {",
				"      \"def\": 456",
				"    },",
				"    {",
				"      \"ghi\": 789",
				"    }",
				"  ]",
				"}"
				));
		
		String json = cut.toJson();
		assertThat(json).isEqualTo(EXPECTED);
	}

}
